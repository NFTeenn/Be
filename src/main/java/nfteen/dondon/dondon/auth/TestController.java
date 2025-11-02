package nfteen.dondon.dondon.auth;


import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.jwt.JWTUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final JWTUtil jwtUtil;

    @GetMapping("/api/test/auth-check")
    public String testAuth() {
        return "인증 성공!";
    }

    @GetMapping("/auth/get-test-token")
    public String getTestToken() {
        return jwtUtil.createTestToken("testuser@example.com");
    }
}
