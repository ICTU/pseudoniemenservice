package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import nl.ictu.model.Identifier;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.crypto.AesGcmSivCryptographer;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganisationPseudoTokenMapper {

    public static final String V_1 = "v1";
    private final AesGcmSivCryptographer aesGcmSivCryptographer;


    /**
     * Maps the provided callerOIN and Token into a WsExchangeTokenResponse object.
     *
     * @param callerOIN the originating identification number of the caller
     * @param token     the Token object containing the required information such as BSN
     * @return a WsExchangeTokenResponse containing an encrypted identifier
     * @throws InvalidCipherTextException if there is an issue with the encryption process
     * @throws IOException                if there is an I/O error during encryption
     */
    public WsExchangeTokenResponse map(final String callerOIN,
            final Token token) throws InvalidCipherTextException, IOException {

        final var tokenIdentifier = Identifier.builder()
                .version(V_1)
                .bsn(token.getBsn())
                .build();
        final var encryptedIdentifier = aesGcmSivCryptographer.encrypt(tokenIdentifier, callerOIN);
        return WsExchangeTokenResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(ORGANISATION_PSEUDO)
                        .value(encryptedIdentifier)
                        .build())
                .build();
    }
}
