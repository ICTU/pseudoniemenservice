package nl.appsource;

import lombok.NoArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
@NoArgsConstructor
public class PseudoniemenServiceApplication {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(final String[] args) {
        SpringApplication.run(PseudoniemenServiceApplication.class, args);
    }
}
