package nfteen.dondon.dondon.auth.controller;


import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.dto.GoogleLoginRequest;
import nfteen.dondon.dondon.auth.dto.TokenResponse;
import nfteen.dondon.dondon.auth.jwt.JWTUtil;
import nfteen.dondon.dondon.auth.service.OAuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {


    private final OAuthService authService;
    private final JWTUtil jwtUtil;

    @PostMapping("/get-token")
    public TokenResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return authService.loginWithGoogle(request.getCredential());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestHeader("Authorization") String refreshToken) {
        return authService.refreshToken(refreshToken.replace("Bearer ", ""));
    }
}
