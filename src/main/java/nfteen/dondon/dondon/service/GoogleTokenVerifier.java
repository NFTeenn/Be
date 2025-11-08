package nfteen.dondon.dondon.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;

@Component
public class GoogleTokenVerifier {

    private final String googleJwkUrl;
    private final RemoteJWKSet<SecurityContext> jwkSet;

    public GoogleTokenVerifier(@Value("${google.jwk.url}") String googleJwkUrl) throws Exception {
        this.googleJwkUrl = googleJwkUrl;
        this.jwkSet = new RemoteJWKSet<>(new URL(googleJwkUrl));
    }

    public boolean verify(String idToken) {
        try {
            System.out.println("Google JWK URL: " + googleJwkUrl);
            System.out.println("ID Token: " + idToken);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

            JWSVerificationKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSet);
            jwtProcessor.setJWSKeySelector(keySelector);

            jwtProcessor.process(SignedJWT.parse(idToken), null);

            return true; // 검증 성공
        }  catch (BadJOSEException e) {
            if (e.getMessage() != null && e.getMessage().contains("Expired JWT")) {
                System.out.println("[GoogleTokenVerifier] 토큰 만료됨");
            } else {
                System.out.println("[GoogleTokenVerifier] JWT 검증 실패: " + e.getMessage());
            }
            return false;
        } catch (JOSEException | ParseException e) {
            System.out.println("[GoogleTokenVerifier] 토큰 파싱/검증 오류: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
