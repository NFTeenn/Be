package nfteen.dondon.dondon.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import nfteen.dondon.dondon.config.RSAKeyGenerator;
import org.springframework.stereotype.Component;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JWTUtil {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JWTUtil(RSAKeyGenerator rsaKeyGenerator) {
        this.privateKey = rsaKeyGenerator.getPrivateKey();
        this.publicKey = rsaKeyGenerator.getPublicKey();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateAccessToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000 * 60 * 60); // 1시간 유효
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000 * 60 * 60); // 1시간 유효
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}