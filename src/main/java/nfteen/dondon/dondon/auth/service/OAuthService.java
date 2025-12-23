package nfteen.dondon.dondon.auth.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.dto.TokenResponse;
import nfteen.dondon.dondon.auth.jwt.JWTUtil;
import nfteen.dondon.dondon.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public TokenResponse loginWithGoogle(String idToken) {
        GoogleUser googleUser = googleTokenVerifier.verify(idToken);

        if (googleUser == null) {
            throw new IllegalArgumentException("Google 인증 실패");
        }

        GoogleUser user = userRepository.findByEmail(googleUser.getEmail()).orElse(null);


        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        user.setRefresh_token(refreshToken);
        userRepository.save(user);

        System.out.println("구글 로그인 성공 - email: " + user.getEmail());
        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshToken(String refreshToken) {
        var claims = jwtUtil.getClaims(refreshToken);
        if (!jwtUtil.validateToken(refreshToken)) {
            System.out.println("리프레시 토큰 유효하지 않음");
            return null;
        }

        String email = claims.getSubject();
        GoogleUser user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !refreshToken.equals(user.getRefresh_token())){
            System.out.println("리프레시 토큰 불일치 - email: " + email);
            return null;
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        user.setRefresh_token(newRefreshToken);
        userRepository.save(user);

        System.out.println("토큰 재발급 완료 - email: " + email);
        System.out.println("Access Token: " + newAccessToken);
        System.out.println("Refresh Token: " + newRefreshToken);


        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}