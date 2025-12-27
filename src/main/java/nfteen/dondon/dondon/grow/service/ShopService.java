package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.dto.AccessaryResponse;
import nfteen.dondon.dondon.grow.dto.BuyAccResponse;
import nfteen.dondon.dondon.grow.dto.LikesResponse;
import nfteen.dondon.dondon.grow.entity.*;
import nfteen.dondon.dondon.grow.repository.AccessaryRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import nfteen.dondon.dondon.grow.repository.UserAccRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final MyInfoRepository myInfoRepository;
    private final AccessaryRepository accessaryRepository;
    private final UserAccRepository userAccRepository;

    public List<AccessaryResponse> getAllAccessaries(Long userId){
        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        List<Accessary> accessaries = accessaryRepository.findAll();
        List<UserAcc> userAccs = userAccRepository.findByMyInfo(info);

        return accessaries.stream()
                .map(acc -> {
                    UserAcc ua = userAccs.stream()
                            .filter(u -> u.getAcc().getId().equals(acc.getId()))
                            .findFirst()
                            .orElse(null);

                    return new AccessaryResponse(
                            acc.getId(),
                            acc.getName(),
                            acc.getDescription(),
                            acc.getPrice(),
                            ua != null,                   // owned
                            ua != null && ua.isEquipped() // equipped
                    );
                })
                .toList();
    }

    @Transactional
    public BuyAccResponse buyAcc(Long userId, Long accId) {
        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        Accessary acc = accessaryRepository.findById(accId)
                .orElseThrow(() -> new IllegalArgumentException("악세서리 정보 없음"));

        if(info.getCoin() < acc.getPrice()){
            return new BuyAccResponse(false, "코인이 부족합니다.");
        }

        if(userAccRepository.existsByMyInfoAndAcc(info, acc)) {
            return new BuyAccResponse(false, "이미 소유한 악세서리입니다.");
        }

        info.setCoin(info.getCoin() - acc.getPrice());

        userAccRepository.save(UserAcc.builder()
                .myInfo(info)
                .acc(acc)
                .equipped(false)
                .build()
        );


        return new BuyAccResponse(true, "구매 완료");
    }

    @Transactional
    public void equipAcc(Long userId, Long accId) {
        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        List<UserAcc> userAccs = userAccRepository.findByMyInfo(info);

        userAccs.stream()
                .filter(UserAcc::isEquipped)
                .forEach(ua -> ua.setEquipped(false));

        UserAcc target = userAccs.stream()
                .filter(ua -> ua.getAcc().getId().equals(accId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("소유하지 않은 악세서리입니다."));

        target.setEquipped(true);
    }

    @Transactional
    public void unequipAcc(Long userId) {
        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("유저 정보 없음"));

        userAccRepository.findByMyInfo(info).stream()
                .filter(UserAcc::isEquipped)
                .forEach(ua -> ua.setEquipped(false));
    }

}
