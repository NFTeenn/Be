package nfteen.dondon.dondon.grow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class HomeLevelUpEvent {
    private String email;
    private int level;
    private int day;
}
