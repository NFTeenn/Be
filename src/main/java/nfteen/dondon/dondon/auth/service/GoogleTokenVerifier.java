package nfteen.dondon.dondon.auth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.entity.Role;
import nfteen.dondon.dondon.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier {

    @Value("${google.jwk.url}")
    private String googleJwkUrl;

    private final UserRepository userRepository;

    public GoogleUser verify(String idToken) {
        try {
            System.out.println("Google JWK URL: " + googleJwkUrl);
            System.out.println("ID Token: " + idToken);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            RemoteJWKSet<SecurityContext> jwkSet = new RemoteJWKSet<>(new URL(googleJwkUrl));
            JWSVerificationKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSet);
            jwtProcessor.setJWSKeySelector(keySelector);

            SignedJWT jwt = SignedJWT.parse(idToken);

            jwtProcessor.process(jwt, null);

            String googleId = jwt.getJWTClaimsSet().getSubject();
            String email = jwt.getJWTClaimsSet().getStringClaim("email");
            String name = jwt.getJWTClaimsSet().getStringClaim("name");

            GoogleUser user = userRepository.findByEmail(email)
                            .map(existing -> {
                                existing.setName(name);
                                existing.setGoogle_id(googleId);
                                return existing;
                            })
                    .orElse(GoogleUser.builder()
                    .google_id(googleId)
                    .email(email)
                    .name(name)
                    .role(Role.USER)
                    .build());

            return userRepository.save(user);
        }  catch (BadJOSEException e) {
            if (e.getMessage() != null && e.getMessage().contains("Expired JWT")) {
                System.out.println("[GoogleTokenVerifier] 토큰 만료됨");
            } else {
                System.out.println("[GoogleTokenVerifier] JWT 검증 실패: " + e.getMessage());
            }
            return null;
        } catch (JOSEException | ParseException e) {
            System.out.println("[GoogleTokenVerifier] 토큰 파싱/검증 오류: " + e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
