package nl.appsource.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageDigestFactoryTest {

    @Test
    @DisplayName("""
        Given a MessageDigestWrapper instance
        When calling getMessageDigestInstance()
        Then the resulting MessageDigest should be SHA-256
        """)
    void getMessageDigestSha256_ShouldReturnSha256Digest() {
        // WHEN
        final MessageDigest digest = MessageDigestFactory.instance();
        // THEN
        assertNotNull(digest, "MessageDigest should not be null");
        assertEquals("SHA-256", digest.getAlgorithm(),
            "Expected the digest algorithm to be SHA-256");
    }
}
