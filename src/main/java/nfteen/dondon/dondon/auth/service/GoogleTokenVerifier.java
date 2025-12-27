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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.entity.Role;
import nfteen.dondon.dondon.auth.repository.UserRepository;
import nfteen.dondon.dondon.grow.entity.Prize;
import nfteen.dondon.dondon.grow.entity.UserPrize;
import nfteen.dondon.dondon.grow.repository.PrizeRepository;
import nfteen.dondon.dondon.grow.repository.UserPrizeRepository;
import nfteen.dondon.dondon.grow.service.AchievementService;
import nfteen.dondon.dondon.grow.service.GrowService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.ParseException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier {

    private final GrowService growService;
    private final AchievementService achievementService;
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
                        existing.setGoogle_id(googleId);
                        existing.setName(name);
                        return existing;
                    })
                    .orElseGet(() -> GoogleUser.builder()
                            .google_id(googleId)
                            .email(email)
                            .name(name)
                            .role(Role.USER)
                            .build()
                    );

            user = userRepository.save(user);

            growService.createUserGrowInfo(user);
            achievementService.achieve(user, "FIRST_DONDON");

            return user;
        } catch (BadJOSEException e) {
            return null;
        } catch (JOSEException | ParseException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
