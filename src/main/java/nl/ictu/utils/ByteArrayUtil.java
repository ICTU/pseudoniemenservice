package nl.ictu.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ByteArrayUtil {

    /**
     * Concatenates two byte arrays into a single byte array.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return a new byte array containing all the elements of the first array followed by all the
     * elements of the second array
     */
    public static byte[] concat(final byte[] a, final byte[] b) {

        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
