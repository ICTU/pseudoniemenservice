package nl.ictu.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;
import nl.ictu.service.exception.IdentifierPrivateKeyException;
import nl.ictu.service.exception.TokenPrivateKeyException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "pseudoniemenservice")
@Data
@Accessors(chain = true)
public final class PseudoniemenServiceProperties {

    private String tokenPrivateKey;

    private String identifierPrivateKey;

    /**
     * Validates that the required private keys for the token and identifier are set.
     *
     * This method performs a post-construction validation of the `PseudoniemenServiceProperties` object to ensure that
     * the `tokenPrivateKey` and `identifierPrivateKey` are properly configured. If either of these properties is not set
     * or is empty, specific exceptions are thrown:
     *
     * - If `tokenPrivateKey` is null or empty, a {@link TokenPrivateKeyException} is thrown.
     * - If `identifierPrivateKey` is null or empty, a {@link IdentifierPrivateKeyException} is thrown.
     *
     * @throws TokenPrivateKeyException if the `tokenPrivateKey` is missing or empty.
     * @throws IdentifierPrivateKeyException if the `identifierPrivateKey` is missing or empty.
     */
    @PostConstruct
    public void validate() {

        if (!StringUtils.hasText(tokenPrivateKey)) {
            throw new TokenPrivateKeyException("Please set a private token key");
        }
        if (!StringUtils.hasText(identifierPrivateKey)) {
            throw new IdentifierPrivateKeyException("Please set a private identifier key");
        }
    }
}

