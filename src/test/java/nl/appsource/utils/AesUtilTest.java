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

class AesUtilTest {

    @Test
    @DisplayName("""
        Given no input
        When generating an IV using AesUtility.generateIV()
        Then a non-null GCMParameterSpec should be returned with the correct IV length and tag length
        """)
    void generateIV_ShouldReturnGCMParameterSpec() {
        // WHEN
        final GCMParameterSpec spec = AesUtil.generateIV();
        // THEN
        assertNotNull(spec, "GCMParameterSpec should not be null");
        assertEquals(AesUtil.TAG_LENGTH, spec.getTLen(),
            "Tag length should be " + AesUtil.TAG_LENGTH + " bits");
        assertNotNull(spec.getIV(), "IV should not be null");
        assertEquals(AesUtil.IV_LENGTH, spec.getIV().length,
            "IV length should be " + AesUtil.IV_LENGTH);
    }

    @Test
    @DisplayName("""
        Given a byte array of IV values
        When creating a GCMParameterSpec using AesUtility.createIVfromValues()
        Then the resulting GCMParameterSpec should match the input IV values
        """)
    void createIVfromValues_ShouldReturnGCMParameterSpecFromGivenIV() {
        // GIVEN: a deterministic IV of length 12
        final byte[] ivBytes = new byte[AesUtil.IV_LENGTH];
        for (int i = 0; i < ivBytes.length; i++) {
            ivBytes[i] = (byte) i;
        }
        // WHEN
        final GCMParameterSpec spec = AesUtil.createIVfromValues(ivBytes);
        // THEN
        assertNotNull(spec, "GCMParameterSpec should not be null");
        assertEquals(AesUtil.TAG_LENGTH, spec.getTLen(),
            "Tag length should be " + AesUtil.TAG_LENGTH + " bits");
        assertArrayEquals(ivBytes, spec.getIV(),
            "IV in GCMParameterSpec should match the input array");
    }

    @Test
    @DisplayName("""
        Given no input
        When creating a Cipher instance using AesUtility.createCipher()
        Then the resulting Cipher should be of type AES/GCM/NoPadding
        """)
    @SneakyThrows
    void createCipher_ShouldReturnAesGcmNoPadding() {
        // WHEN
        final Cipher cipher = AesUtil.createCipher();
        // THEN
        assertNotNull(cipher, "Cipher should not be null");
        assertEquals("AES/GCM/NoPadding", cipher.getAlgorithm(),
            "Expected the cipher algorithm to be AES/GCM/NoPadding");
    }

    @Test
    @DisplayName("""
        Given no input
        When retrieving the AES engine using AesUtility.getAESEngine()
        Then the resulting engine should be an instance of AESEngine
        """)
    void getAESEngine_ShouldReturnAesEngine() {
        // WHEN
        final MultiBlockCipher engine = AesUtil.getAESEngine();
        // THEN
        assertNotNull(engine, "Engine should not be null");
        assertInstanceOf(AESEngine.class, engine, "Engine should be an instance of AESEngine");
    }
}
