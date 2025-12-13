package nfteen.dondon.dondon.grow.event;

import lombok.Getter;

@Getter
public class UserCreateEvent {
    private final Long userId;

    public UserCreateEvent(Long userId) {
        this.userId = userId;
    }
}
