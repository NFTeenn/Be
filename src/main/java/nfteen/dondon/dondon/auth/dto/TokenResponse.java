package nfteen.dondon.dondon.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String access_token;
    private String refresh_token;
}
