package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BuyAccRequest {
    private Long userId;
    private Long accId;
}
