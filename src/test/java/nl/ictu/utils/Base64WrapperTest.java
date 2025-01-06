package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Base64WrapperTest {

    private Base64Wrapper base64Wrapper;

    @BeforeEach
    void setUp() {
        base64Wrapper = new Base64Wrapper();
    }

    @Test
    @DisplayName("""
            Given a byte array of the string "Hello"
            When encoding the byte array using Base64Wrapper.encode()
            Then the result should be the Base64-encoded string "SGVsbG8="
            """)
    void encode_ShouldEncodeBytesToBase64Bytes() {
        // GIVEN
        final var input = "Hello".getBytes(StandardCharsets.UTF_8);
        // WHEN
        final var result = base64Wrapper.encode(input);
        // THEN
        final var resultAsString = new String(result, StandardCharsets.UTF_8);
        assertEquals("SGVsbG8=", resultAsString,
                "Expected Base64 encoding of 'Hello' to be 'SGVsbG8='");
    }

    @Test
    @DisplayName("""
            Given a byte array of the string "Hello"
            When encoding the byte array using Base64Wrapper.encodeToString()
            Then the result should be the Base64-encoded string "SGVsbG8="
            """)
    void encodeToString_ShouldEncodeBytesToBase64String() {
        // GIVEN
        final var input = "Hello".getBytes(StandardCharsets.UTF_8);
        // WHEN
        final var base64String = base64Wrapper.encodeToString(input);
        // THEN
        assertEquals("SGVsbG8=", base64String,
                "Expected Base64 encoding of 'Hello' to be 'SGVsbG8='");
    }

    @Test
    @DisplayName("""
            Given a Base64-encoded string "SGVsbG8="
            When decoding the string using Base64Wrapper.decode()
            Then the result should be the decoded byte array representing "Hello"
            """)
    void decode_ShouldDecodeBase64StringToBytes() {
        // GIVEN
        final var base64String = "SGVsbG8=";
        // WHEN
        final var decoded = base64Wrapper.decode(base64String);
        // THEN
        final var decodedAsString = new String(decoded, StandardCharsets.UTF_8);
        assertEquals("Hello", decodedAsString,
                "Expected Base64 decoding of 'SGVsbG8=' to be 'Hello'");
    }

    @Test
    @DisplayName("""
            Given an invalid Base64 string "Not valid base64!!!"
            When attempting to decode using Base64Wrapper.decode()
            Then an IllegalArgumentException should be thrown
            """)
    void decode_ShouldThrowException_WhenInvalidBase64String() {
        // GIVEN
        final var invalidBase64 = "Not valid base64!!!";
        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
                () -> base64Wrapper.decode(invalidBase64),
                "Expected decode() to throw IllegalArgumentException for invalid Base64 string");
    }
}
