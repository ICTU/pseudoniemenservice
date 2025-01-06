package nl.ictu.utils;

import java.security.MessageDigest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public final class MessageDigestWrapper {

    public static final String SHA_256 = "SHA-256";

    /**
     * Creates and returns a new instance of the MessageDigest configured for the SHA-256 algorithm.
     *
     * @return a MessageDigest instance initialized to use the SHA-256 algorithm
     */
    @SneakyThrows
    public MessageDigest getMessageDigestInstance() {

        return MessageDigest.getInstance(SHA_256);
    }
}
