package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.grow.dto.DondonInfoResponse;
import nfteen.dondon.dondon.grow.dto.MyInfoResponse;
import nfteen.dondon.dondon.grow.dto.MyPageResponse;
import nfteen.dondon.dondon.grow.entity.DondonInfo;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.entity.UserAcc;
import nfteen.dondon.dondon.grow.repository.DondonInfoRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import nfteen.dondon.dondon.grow.repository.UserAccRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GrowService {

    private final MyInfoRepository myInfoRepository;
    private final DondonInfoRepository dondonInfoRepository;
    private final UserAccRepository userAccRepository;

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
                        // ✅ 동시 요청으로 이미 생성된 경우 재조회
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
                .findByMyInfoAndEquippedTrue(myInfo)
                .orElse(null);

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

}
