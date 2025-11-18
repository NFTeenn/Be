package nfteen.dondon.dondon.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicRequest {
    private String token;
    private String email;
}
