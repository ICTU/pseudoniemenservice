package nl.ictu.service.map;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import nl.ictu.crypto.AesGcmCryptographer;
import nl.ictu.crypto.TokenCoder;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsGetTokenResponseMapper {

    public static final String V_1 = "v1";
    private final AesGcmCryptographer aesGcmCryptographer;
    private final TokenCoder tokenCoder;

    /**
     * Maps input parameters to a WsGetTokenResponse object. This involves creating a Token object
     * with the given input parameters, encoding it, encrypting the encoded result, and incorporating
     * it into a WsGetTokenResponse object.
     *
     * @param bsn the BSN value to be included in the Token object
     * @param creationDate the creation date to be included in the Token object
     * @param recipientOIN the recipient OIN value used in the Token object and for encryption
     * @return a WsGetTokenResponse object containing the encrypted token
     * @throws IOException if an I/O error occurs during the encoding process
     * @throws IllegalBlockSizeException if the block size is invalid during the encryption process
     * @throws BadPaddingException if an error occurs with padding during encryption
     * @throws InvalidAlgorithmParameterException if algorithm parameters are invalid
     * @throws InvalidKeyException if the encryption key is invalid
     * @throws NoSuchAlgorithmException if the encryption algorithm is not available
     * @throws NoSuchPaddingException if the padding scheme is not available
     */
    public WsGetTokenResponse map(final String bsn, final long creationDate,
            final String recipientOIN)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

        return WsGetTokenResponse.builder()
                .token(aesGcmCryptographer.encrypt(tokenCoder.encode(Token.builder()
                        .version(V_1)
                        .bsn(bsn)
                        .creationDate(creationDate)
                        .recipientOIN(recipientOIN)
                        .build()), recipientOIN))
                .build();
    }
}
