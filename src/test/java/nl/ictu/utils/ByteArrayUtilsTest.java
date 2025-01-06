package nl.ictu.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ByteArrayUtilsTest {

    @Test
    @DisplayName("""
            Given two non-empty byte arrays [1, 2, 3] and [4, 5, 6]
            When concatenating the arrays using ByteArrayUtil.concat()
            Then the result should be a single byte array [1, 2, 3, 4, 5, 6]
            """)
    void concat_ShouldConcatenateTwoArrays() {
        // GIVEN
        byte[] a = {1, 2, 3};
        byte[] b = {4, 5, 6};
        byte[] expected = {1, 2, 3, 4, 5, 6};
        // WHEN
        byte[] result = ByteArrayUtil.concat(a, b);
        // THEN
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("""
            Given two empty byte arrays
            When concatenating the arrays using ByteArrayUtil.concat()
            Then the result should be an empty byte array
            """)
    void concat_ShouldHandleTwoEmptyArrays() {
        // GIVEN
        byte[] a = {};
        byte[] b = {};
        byte[] expected = {};
        // WHEN
        byte[] result = ByteArrayUtil.concat(a, b);
        // THEN
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("""
            Given one empty byte array and one non-empty byte array
            When concatenating the arrays using ByteArrayUtil.concat()
            Then the result should be the non-empty array
            """)
    void concat_ShouldHandleOneEmptyArray() {
        // GIVEN
        byte[] a = {1, 2, 3};
        byte[] b = {};
        byte[] expected1 = {1, 2, 3};
        // WHEN
        byte[] result1 = ByteArrayUtil.concat(a, b);
        // THEN
        assertArrayEquals(expected1, result1);
        // GIVEN
        byte[] c = {};
        byte[] d = {4, 5, 6};
        byte[] expected2 = {4, 5, 6};
        // WHEN
        byte[] result2 = ByteArrayUtil.concat(c, d);
        // THEN
        assertArrayEquals(expected2, result2);
    }
}
