package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.grow.entity.Prize;
import nfteen.dondon.dondon.grow.entity.UserPrize;
import nfteen.dondon.dondon.grow.repository.PrizeRepository;
import nfteen.dondon.dondon.grow.repository.UserPrizeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final PrizeRepository prizeRepository;
    private final UserPrizeRepository userPrizeRepository;

    @Transactional
    public void achieve(GoogleUser user, String prizeCode){
        Prize prize = prizeRepository.findByCode(prizeCode)
                .orElseThrow(()->new IllegalArgumentException("Prize 없음"));

        try {
            userPrizeRepository.save(
                    UserPrize.builder()
                            .user(user)
                            .prize(prize)
                            .achieved(true)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
        }
    }
}
