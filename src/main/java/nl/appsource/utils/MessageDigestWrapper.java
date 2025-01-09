package nl.appsource.utils;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public final class MessageDigestWrapper {

    private static final String SHA_256 = "SHA-256";
    private MessageDigest messageDigest;

    @PostConstruct
    private void init() {
        try {
            messageDigest = MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            // fail early, fail hard
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and returns a new instance of the MessageDigest configured for the SHA-256 algorithm.
     *
     * @return a MessageDigest instance initialized to use the SHA-256 algorithm
     */
    @SneakyThrows
    public MessageDigest instance() {
        return messageDigest;
    }
}
