package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.grow.dto.*;
import nfteen.dondon.dondon.grow.entity.*;
import nfteen.dondon.dondon.grow.repository.*;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GrowService {

    private final MyInfoRepository myInfoRepository;
    private final DondonInfoRepository dondonInfoRepository;
    private final UserAccRepository userAccRepository;
    private final LikesRepository likesRepository;
    private final UserPrizeRepository userPrizeRepository;
    private final AccessaryRepository accessaryRepository;
    private final ListableBeanFactory listableBeanFactory;

    @Transactional
    public MyInfo createUserGrowInfo(GoogleUser user) {

        return myInfoRepository.findByUserId(user.getId())
                .orElseGet(() -> {

                    MyInfo info = MyInfo.builder()
                            .user(user)
                            .username(user.getName())
                            .email(user.getEmail())
                            .days(1)
                            .quizStack(0)
                            .newsStack(0)
                            .recentGen(1)
                            .coin(0)
                            .build();

                    try {
                        MyInfo saved = myInfoRepository.save(info);
                        createDefaultDondon(saved);
                        return saved;

                    } catch (DataIntegrityViolationException e) {
                        return myInfoRepository.findByUserId(user.getId())
                                .orElseThrow(() -> e);
                    }
                });
    }

    @Transactional
    public DondonInfo createDefaultDondon(MyInfo info) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        DondonInfo dondon = DondonInfo.builder()
                .myInfo(info)
                .gen(info.getRecentGen())
                .nickname("돈돈이")
                .level(0)
                .enterDate(today)
                .style(0)
                .build();

        userPrizeRepository.updateAchieved(info.getUser().getId(), "FIRST_DONDON");

        return dondonInfoRepository.save(dondon);
    }

    @Transactional
    public MyPageResponse getMyPageInfo(GoogleUser user) {

        MyInfo myInfo = myInfoRepository.findByUserId(user.getId())
                .orElseGet(()-> createUserGrowInfo(user));

        MyInfoResponse myInfoResponse = new MyInfoResponse(
                myInfo.getUsername(),
                myInfo.getDays(),
                myInfo.getQuizStack(),
                myInfo.getNewsStack(),
                myInfo.getRecentGen(),
                myInfo.getCoin()
        );

        DondonInfo latestDondon = dondonInfoRepository
                .findTopByMyInfoOrderByGenDesc(myInfo)
                .orElseThrow(()->new IllegalArgumentException("돈돈이가 존재하지 않음"));

        UserAcc equippedAcc = userAccRepository
                .findByMyInfoAndEquippedTrue(myInfo);

        Long accId = equippedAcc == null ? null : equippedAcc.getAcc().getId();

        DondonInfoResponse dondonInfoResponse = new DondonInfoResponse(
                latestDondon.getGen(),
                latestDondon.getNickname(),
                latestDondon.getLevel(),
                latestDondon.getEnterDate(),
                latestDondon.getGraduationDate(),
                latestDondon.getStyle(),
                accId
        );
        return new MyPageResponse(myInfoResponse, dondonInfoResponse);
    }

    @Transactional(readOnly = true)
    public List<DondonInfo> getGraduatedDonDons(Long userId) {
        return dondonInfoRepository
                .findByMyInfo_UserIdAndGraduationDateIsNotNull(userId);
    }

    @Transactional
    public DondonInfo graduateAndAdopt(MyInfo info){
        DondonInfo current = dondonInfoRepository
                .findTopByMyInfoOrderByGenDesc(info)
                .orElseThrow(() -> new IllegalStateException("돈돈이 없음"));

        if (current.getLevel() != 20){
            throw new IllegalStateException("최고 레벨에 도달하지 못했습니다.");
        }

        current.setGraduationDate(LocalDate.now(ZoneId.of("Asia/Seoul")));

        info.setRecentGen(current.getGen() + 1);

        return createDefaultDondon(info);
    }

    @Transactional
    public void changeDondonName(Long userId, String newNickname) {
        DondonInfo dondon = dondonInfoRepository
                .findByMyInfo_UserIdAndGraduationDateIsNull(userId)
                .orElseThrow(() -> new IllegalStateException("현재 성장 중인 돈돈이가 없습니다."));

        dondon.setNickname(newNickname);
    }

    @Transactional
    public boolean saveLike(Long userId, Long targetId, TypeName type) {
        boolean exists = likesRepository.existsByMyInfo_UserIdAndTargetIdAndType(userId,targetId,type);

        if (exists) {
            likesRepository.deleteByMyInfo_UserIdAndTargetIdAndType(userId, targetId, type);
            return false;
        }


        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));


        Like like = Like.builder()
                .myInfo(info)
                .targetId(targetId)
                .type(type)
                .build();
        try{
            likesRepository.save(like);
            return true;
        } catch (DataIntegrityViolationException e) {
            likesRepository.deleteByMyInfo_UserIdAndTargetIdAndType(userId, targetId, type);
            return false;
        }
    }

    public List<LikesResponse> getLikes(Long userId, TypeName type) {
        List<Like> list;

        if(type == null) {
            list = likesRepository.findByMyInfo_UserId(userId);
        } else{
            list = likesRepository.findByMyInfo_UserIdAndType(userId, type);
        }

        return list.stream()
                .map(l -> new LikesResponse(l.getTargetId(), l.getType().name()))
                .toList();
    }

    @Transactional
    public List<PrizeResponse> getPrizes(Long userId) {
        List<UserPrize> userPrizes = userPrizeRepository.findByUserId(userId);

        return userPrizes.stream()
                .map(up -> new PrizeResponse(
                        up.getPrize().getCode(),
                        up.getPrize().getTitle(),
                        up.getPrize().getDescription(),
                        up.isAchieved()
                ))
                .toList();
    }

    public List<AccessaryResponse> getAllAccessaries(){
        List<Accessary> accessaries = accessaryRepository.findAll();
        return accessaries.stream()
                .map(acc -> new AccessaryResponse(acc.getId(), acc.getName(), acc.getDescription(), acc.getPrice()))
                .collect(Collectors.toList());
    }

    @Transactional
    public BuyAccResponse buyAcc(BuyAccRequest request) {
        MyInfo info = myInfoRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        Accessary acc = accessaryRepository.findById(request.getAccId())
                .orElseThrow(() -> new IllegalArgumentException("악세서리 정보 없음"));

        if(info.getCoin() < acc.getPrice()){
            return new BuyAccResponse(false, "코인이 부족합니다.");
        }

        boolean alreadyOwned = userAccRepository.existsByMyInfoAndAcc(info, acc);
        if(alreadyOwned) {
            return new BuyAccResponse(false, "이미 소유한 악세서리입니다.");
        }

        info.setCoin(info.getCoin() - acc.getPrice());
        myInfoRepository.save(info);

        UserAcc userAcc = UserAcc.builder()
                .myInfo(info)
                .acc(acc)
                .equipped(true)
                .build();
        userAccRepository.save(userAcc);

        return new BuyAccResponse(true, "구매 완료");
    }


}
