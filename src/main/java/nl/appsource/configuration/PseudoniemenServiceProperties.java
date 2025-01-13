package nl.appsource.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "pseudoniemenservice")
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public final class PseudoniemenServiceProperties {

    private String tokenPrivateKey;

    private String identifierPrivateKey;

    /**
     * Validates that the required private keys for the token and identifier are set.
     * <p>
     * This method performs a post-construction validation of the `PseudoniemenServiceProperties` object to ensure that
     * the `tokenPrivateKey` and `identifierPrivateKey` are properly configured. If either of these properties is not set
     * or is empty, specific exceptions are thrown:
     */
    @PostConstruct
    public void validate() {

        if (!StringUtils.hasText(tokenPrivateKey)) {
            throw new RuntimeException("Please set a private token key");
        }
        if (!StringUtils.hasText(identifierPrivateKey)) {
            throw new RuntimeException("Please set a private identifier key");
        }
    }
}

