package nl.appsource.utils;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public final class MessageDigestFactory {

    private static final String SHA_256 = "SHA-256";

    /**
     * Creates and returns a new instance of the MessageDigest configured for the SHA-256 algorithm.
     *
     * @return a MessageDigest instance initialized to use the SHA-256 algorithm
     */

    public static MessageDigest instance() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
