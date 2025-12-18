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

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GrowLevelEventListener {
    private final MyInfoRepository myInfoRepository;
    private final DondonInfoRepository dondonInfoRepository;

    @EventListener
    @Transactional
    public void handleHomeLevelUp(HomeLevelUpEvent event) {
        Optional<MyInfo> myInfoOpt = myInfoRepository.findByEmail(event.getEmail());
        if(myInfoOpt.isEmpty()){
            return;
        }

        MyInfo myInfo = myInfoOpt.get();

        Optional<DondonInfo> dondonOpt = dondonInfoRepository
                .findByMyInfo_UserIdAndGraduationDateIsNull(myInfo.getUserId());

        if(dondonOpt.isEmpty()){
            return;
        }

        DondonInfo dondon = dondonOpt.get();

        dondon.setLevel(event.getLevel());
        myInfo.setCoin(myInfo.getCoin() + 1);
        myInfo.setDays(event.getDay());
    }
}
