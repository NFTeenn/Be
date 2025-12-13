package nfteen.dondon.dondon.grow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeLevelUpEvent {
    private String email;
    private int level;
}
