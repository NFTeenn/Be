package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.grow.dto.DondonInfoResponse;
import nfteen.dondon.dondon.grow.dto.MyInfoResponse;
import nfteen.dondon.dondon.grow.dto.MyPageResponse;
import nfteen.dondon.dondon.grow.entity.DondonInfo;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.repository.DondonInfoRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GrowService {

    private final MyInfoRepository myInfoRepository;
    private final DondonInfoRepository dondonInfoRepository;

    @Transactional
    public MyInfo createUserGrowInfo(GoogleUser user) {

        Optional<MyInfo> exist = myInfoRepository.findByUser(user);
        if(exist.isPresent()) {
            return exist.get();
        }

        MyInfo info = MyInfo.builder()
                .user(user)
                .username(user.getName())
                .days(1)
                .quizStack(0)
                .newsStack(0)
                .recentGen(1)
                .coin(0)
                .build();

        myInfoRepository.save(info);

        createDefaultDondon(info);

        return info;
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
                .acc(null)
                .build();

        return dondonInfoRepository.save(dondon);
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(GoogleUser user) {

        MyInfo myInfo = myInfoRepository.findByUser(user)
                .orElseThrow(()-> new IllegalArgumentException("MyInfo not found"));

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

        DondonInfoResponse dondonInfoResponse = new DondonInfoResponse(
                latestDondon.getGen(),
                latestDondon.getNickname(),
                latestDondon.getLevel(),
                latestDondon.getEnterDate(),
                latestDondon.getGraduationDate(),
                latestDondon.getStyle(),
                latestDondon.getAcc() != null ? latestDondon.getAcc().getId() : null
        );
        return new MyPageResponse(myInfoResponse, dondonInfoResponse);
    }

}
