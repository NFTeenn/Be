package nfteen.dondon.dondon.config;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class RSAKeyGenerator {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public RSAKeyGenerator() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        this.privateKey = (RSAPrivateKey)pair.getPrivate();
        this.publicKey = (RSAPublicKey)pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
