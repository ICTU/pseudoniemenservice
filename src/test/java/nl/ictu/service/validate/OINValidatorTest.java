package nl.ictu.service.validate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.ictu.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OINValidatorTest {

    private OINValidator oinValidator;

    @BeforeEach
    void setUp() {

        oinValidator = new OINValidator();
    }

    @Test
    @DisplayName("""
            Given a caller OIN and a token with matching recipientOIN
            When isValid() is called
            Then it should return true
            """)
    void isValid_ReturnsTrue_WhenOINsMatch() {
        // GIVEN
        final var callerOIN = "TEST_OIN_123";
        final var token = Token.builder()
                .recipientOIN("TEST_OIN_123")
                .build();
        // WHEN
        final var result = oinValidator.isValid(callerOIN, token);
        // THEN
        assertTrue(result, "Expected isValid() to return true for matching OINs");
    }

    @Test
    @DisplayName("""
            Given a caller OIN and a token with non-matching recipientOIN
            When isValid() is called
            Then it should return false
            """)
    void isValid_ReturnsFalse_WhenOINsDoNotMatch() {
        // GIVEN
        final var callerOIN = "TEST_OIN_ABC";
        final var token = Token.builder()
                .recipientOIN("TEST_OIN_XYZ")
                .build();
        // WHEN
        final var result = oinValidator.isValid(callerOIN, token);
        // THEN
        assertFalse(result, "Expected isValid() to return false for non-matching OINs");
    }

    @Test
    @DisplayName("""
            Given a caller OIN and a token with a null recipientOIN
            When isValid() is called
            Then it should return false
            """)
    void isValid_Behavior_WhenTokenRecipientOINIsNull() {
        // GIVEN
        final var callerOIN = "NON_NULL_OIN";
        final var token = Token.builder().build();
        // WHEN
        final var result = oinValidator.isValid(callerOIN, token);
        // THEN
        assertFalse(result, "Expected isValid() to return false if token's recipientOIN is null");
    }
}
