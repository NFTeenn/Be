package nfteen.dondon.dondon.grow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreateEvent {
    private final Long userId;
}
