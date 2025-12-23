package nfteen.dondon.dondon.grow.listner;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.event.QuizSolvedEvent;
import nfteen.dondon.dondon.grow.service.GrowService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QuizSolvedEventListener {
    private final GrowService growService;

    @EventListener
    public void handleQuizSolvedEvent(QuizSolvedEvent event){
        growService.increaseQuizStack(event.getEmail());
    }
}
