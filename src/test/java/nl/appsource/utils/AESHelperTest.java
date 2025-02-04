package nl.appsource.utils;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AESHelperTest {

    @Test
    @DisplayName("""
        Given no input
        When generating an IV using AesUtility.generateIV()
        Then a non-null GCMParameterSpec should be returned with the correct IV length and tag length
        """)
    void generateIV_ShouldReturnGCMParameterSpec_WithNonNullIV() {
        // WHEN
        final GCMParameterSpec gcmParameterSpec = AesUtil.generateIV();
        // THEN
        assertNotNull(gcmParameterSpec, "GCMParameterSpec should not be null");
        assertEquals(AesUtil.TAG_LENGTH, gcmParameterSpec.getTLen(),
            "Tag length should be 128 (bits)");
        // The IV array is extracted from gcmParameterSpec
        final byte[] iv = gcmParameterSpec.getIV();
        assertNotNull(iv, "IV should not be null");
        assertEquals(AesUtil.IV_LENGTH, iv.length,
            "IV length should be " + AesUtil.IV_LENGTH);
    }

    @Test
    @DisplayName("""
        Given a byte array of IV values
        When creating a GCMParameterSpec using AesUtility.createIVfromValues()
        Then the resulting GCMParameterSpec should match the input IV values
        """)
    void createIVfromValues_ShouldReturnGCMParameterSpec_FromGivenIV() {
        // GIVEN
        final byte[] ivSource = new byte[AesUtil.IV_LENGTH];
        // Fill the array with deterministic data for test
        for (int i = 0; i < ivSource.length; i++) {
            ivSource[i] = (byte) i;
        }
        // WHEN
        final GCMParameterSpec spec = AesUtil.createIVfromValues(ivSource);
        // THEN
        assertNotNull(spec, "GCMParameterSpec should not be null");
        assertEquals(AesUtil.TAG_LENGTH, spec.getTLen(),
            "Tag length should be 128 (bits)");
        assertArrayEquals(ivSource, spec.getIV(),
            "IV array in GCMParameterSpec should match the input");
    }

    @Test
    @DisplayName("""
        Given no input
        When creating a Cipher instance using AesUtility.createCipher()
        Then the resulting Cipher should be of type AES/GCM/NoPadding
        """)
    @SneakyThrows
    void createCipher_ShouldReturnAesGcmNoPaddingCipher() {
        // WHEN
        final Cipher cipher = AesUtil.createCipher();
        // THEN
        assertNotNull(cipher, "Cipher should not be null");
        // Depending on the JVM/provider, the algorithm name can be uppercase or some variation,
        // but typically you'd expect "AES/GCM/NoPadding".
        assertEquals("AES/GCM/NoPadding", cipher.getAlgorithm(),
            "Cipher algorithm should match AES/GCM/NoPadding");
    }

    @Test
    @DisplayName("""
        Given no input
        When retrieving the AES engine using AesUtility.getAESEngine()
        Then the resulting engine should be an instance of AESEngine
        """)
    void getAESEngine_ShouldReturnNonNullAESEngineInstance() {
        // WHEN
        final MultiBlockCipher engine = AesUtil.getAESEngine();
        // THEN
        assertNotNull(engine, "Engine should not be null");
        assertInstanceOf(AESEngine.class, engine, "Engine should be an instance of AESEngine");
    }
}
