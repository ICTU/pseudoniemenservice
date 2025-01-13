package nl.appsource.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base64UtilTest {

    @Test
    @DisplayName("""
        Given a byte array of the string "Hello"
        When encoding the byte array using Base64Wrapper.encode()
        Then the result should be the Base64-encoded string "SGVsbG8="
        """)
    void encode_ShouldEncodeBytesToBase64Bytes() {
        // GIVEN
        final byte[] input = "Hello".getBytes(StandardCharsets.UTF_8);
        // WHEN
        final byte[] result = Base64Util.encode(input);
        // THEN
        final String resultAsString = new String(result, StandardCharsets.UTF_8);
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
        final byte[] input = "Hello".getBytes(StandardCharsets.UTF_8);
        // WHEN
        final String base64String = Base64Util.encodeToString(input);
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
        final String base64String = "SGVsbG8=";
        // WHEN
        final byte[] decoded = Base64Util.decode(base64String);
        // THEN
        final String decodedAsString = new String(decoded, StandardCharsets.UTF_8);
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
        final String invalidBase64 = "Not valid base64!!!";
        // WHEN & THEN
        assertThrows(IllegalArgumentException.class,
            () -> Base64Util.decode(invalidBase64),
            "Expected decode() to throw IllegalArgumentException for invalid Base64 string");
    }
}
