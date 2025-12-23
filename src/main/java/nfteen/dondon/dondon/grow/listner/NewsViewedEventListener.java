package nfteen.dondon.dondon.grow.listner;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.event.NewsViewedEvent;
import nfteen.dondon.dondon.grow.service.GrowService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsViewedEventListener {
    private final GrowService growService;

    @EventListener
    public void handleNewsViewed(NewsViewedEvent event){
        growService.increaseNewsStack(event.getEmail());
    }
}
