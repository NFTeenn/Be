package nfteen.dondon.dondon.grow.listner;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.repository.UserRepository;
import nfteen.dondon.dondon.grow.entity.Prize;
import nfteen.dondon.dondon.grow.entity.UserPrize;
import nfteen.dondon.dondon.grow.event.UserCreateEvent;
import nfteen.dondon.dondon.grow.repository.PrizeRepository;
import nfteen.dondon.dondon.grow.repository.UserPrizeRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCreateEventListener {
    private final UserRepository userRepository;
    private final PrizeRepository prizeRepository;
    private final UserPrizeRepository userPrizeRepository;

    @EventListener
    public void handleUserCreateEvent(UserCreateEvent event) {
        Long userId = event.getUserId();
        GoogleUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<Prize> prizes = prizeRepository.findAll();

        List<UserPrize> list = prizes.stream()
                .map(prize -> UserPrize.builder()
                        .user(user)
                        .prize(prize)
                        .achieved(false)
                        .build())
                .toList();
        userPrizeRepository.saveAll(list);
    }

}
