package nfteen.dondon.dondon.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordOneRequest {
    private String token;
    private String email;
    private int num;
}
