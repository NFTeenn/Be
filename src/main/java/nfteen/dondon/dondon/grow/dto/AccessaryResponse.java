package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessaryResponse {
    private Long id;
    private String name;
    private String description;
    private int price;
}