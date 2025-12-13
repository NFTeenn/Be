package nfteen.dondon.dondon.grow.listner;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.entity.DondonInfo;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.event.HomeLevelUpEvent;
import nfteen.dondon.dondon.grow.repository.DondonInfoRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GrowLevelEventListener {
    private final MyInfoRepository myInfoRepository;
    private final DondonInfoRepository dondonInfoRepository;

    @EventListener
    @Transactional
    public void handleHomeLevelUp(HomeLevelUpEvent event) {
        MyInfo myInfo = myInfoRepository.findByEmail(event.getEmail())
                .orElseThrow();

        DondonInfo dondon = dondonInfoRepository
                .findByMyInfo_UserIdAndGraduationDateIsNull(myInfo.getUserId())
                .orElseThrow();

        dondon.setLevel(event.getLevel());
        myInfo.setCoin(myInfo.getCoin() + 1);
    }
}
