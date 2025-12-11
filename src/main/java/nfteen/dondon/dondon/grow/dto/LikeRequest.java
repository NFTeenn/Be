package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nfteen.dondon.dondon.grow.entity.TypeName;

@Getter
@AllArgsConstructor
public class LikeRequest {
    private Long targetId;
    private TypeName type;
}
