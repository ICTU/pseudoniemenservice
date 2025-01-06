package nl.ictu.utils;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import org.springframework.stereotype.Component;

@Component
public final class Base64Wrapper {

    public static final Decoder DECODER = Base64.getDecoder();
    public static final Encoder ENCODER = Base64.getEncoder();

    /**
     * Decodes a Base64-encoded string into its original byte array representation.
     *
     * @param toDecode the Base64-encoded string to be decoded
     * @return a byte array containing the decoded data
     */
    public byte[] decode(final String toDecode) {

        return DECODER.decode(toDecode);
    }

    /**
     * Encodes a byte array into its Base64-encoded byte array representation.
     *
     * @param toEncode the byte array to be encoded
     * @return a byte array containing the Base64-encoded representation of the input byte array
     */
    public byte[] encode(final byte[] toEncode) {

        return ENCODER.encode(toEncode);
    }

    /**
     * Encodes the given byte array into a Base64-encoded string.
     *
     * @param toEncode the byte array to be encoded
     * @return a String containing the Base64-encoded representation of the input byte array
     */
    public String encodeToString(final byte[] toEncode) {

        return ENCODER.encodeToString(toEncode);
    }
}
