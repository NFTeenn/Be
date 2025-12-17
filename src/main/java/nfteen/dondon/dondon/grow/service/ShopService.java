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

    public List<AccessaryResponse> getAllAccessaries(){
        List<Accessary> accessaries = accessaryRepository.findAll();
        return accessaries.stream()
                .map(acc -> new AccessaryResponse(acc.getId(), acc.getName(), acc.getDescription(), acc.getPrice()))
                .collect(Collectors.toList());
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
