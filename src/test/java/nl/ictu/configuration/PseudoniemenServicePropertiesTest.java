package nl.ictu.configuration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.ictu.service.exception.IdentifierPrivateKeyException;
import nl.ictu.service.exception.TokenPrivateKeyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PseudoniemenServicePropertiesTest {

    @Test
    @DisplayName("""
                    Given an empty token private key
                    When validating
                    When TokenPrivateKeyException is thrown
            """)
    void validate_WhenTokenPrivateKeyIsEmpty_ThrowsTokenPrivateKeyException() {
        // GIVEN
        final var props = new PseudoniemenServiceProperties()
                .setTokenPrivateKey("")
                .setIdentifierPrivateKey("someIdentifierKey");
        // WHEN & THEN
        assertThrows(TokenPrivateKeyException.class, props::validate);
    }

    @Test
    @DisplayName("""
            Given an empty identifier private key
            When validating, then IdentifierPrivateKeyException is thrown
            """)
    void validate_WhenIdentifierPrivateKeyIsEmpty_ThrowsIdentifierPrivateKeyException() {
        // GIVEN
        final var props = new PseudoniemenServiceProperties()
                .setTokenPrivateKey("someTokenKey")
                .setIdentifierPrivateKey("");
        // WHEN & THEN
        assertThrows(IdentifierPrivateKeyException.class, props::validate);
    }

    @Test
    @DisplayName("""
            Given both keys are set when validating
            Then no exception is thrown
            """)
    void validate_WhenBothKeysAreSet_NoExceptionIsThrown() {
        // GIVEN
        final var props = new PseudoniemenServiceProperties()
                .setTokenPrivateKey("someTokenKey")
                .setIdentifierPrivateKey("someIdentifierKey");
        // WHEN & THEN
        assertDoesNotThrow(props::validate);
    }
}
