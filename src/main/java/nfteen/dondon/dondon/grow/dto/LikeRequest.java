package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeRequest {
    private int targetId;
    private String word;
    private String description;
}
