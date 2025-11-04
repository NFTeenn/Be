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

    public GoogleTokenVerifier(@Value("${google.jwk.url}") String googleJwkUrl) {
        this.googleJwkUrl = googleJwkUrl;
    }

    public boolean verify(String idToken) {
        try {
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

            // 구글의 JWK 세트 (공개키 집합)
            RemoteJWKSet<SecurityContext> jwkSet = new RemoteJWKSet<>(new URL(googleJwkUrl));
            JWSVerificationKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSet);

            jwtProcessor.setJWSKeySelector(keySelector);

            // 토큰 파싱 및 검증
            jwtProcessor.process(SignedJWT.parse(idToken), null);

            return true; // 검증 성공
        } catch (BadJOSEException | JOSEException | ParseException e) {
            e.printStackTrace();
            return false; // 검증 실패
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
