package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrizeResponse {
    private String code;
    private String title;
    private String description;
    private boolean achieved;
}
