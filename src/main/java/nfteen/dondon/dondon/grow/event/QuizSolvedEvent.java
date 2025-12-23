package nfteen.dondon.dondon.grow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizSolvedEvent {
    private final String email;
}
