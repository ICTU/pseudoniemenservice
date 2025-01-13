package nl.appsource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.model.Token;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.appsource.service.crypto.AesGcmCryptographerService;
import nl.appsource.service.crypto.TokenConverter;
import nl.appsource.service.map.BsnTokenMapper;
import nl.appsource.service.map.OrganisationPseudoTokenMapper;
import nl.appsource.service.validate.OINValidator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenService {

    private final AesGcmCryptographerService aesGcmCryptographerService;
    private final TokenConverter tokenConverter;
    private final OINValidator oinValidator;
    private final OrganisationPseudoTokenMapper organisationPseudoTokenMapper;
    private final BsnTokenMapper bsnTokenMapper;


    /**
     * Exchanges a token for an identifier based on the provided request and caller OIN.
     *
     * @param callerOIN                           the originating organization's identification
     *                                            number used for validation
     * @param wsExchangeTokenForIdentifierRequest the request containing the token and identifier
     *                                            type
     * @return a WsExchangeTokenResponse containing the generated or resolved identifier
     * @throws InvalidOINException               if the caller OIN is not valid or does not match
     *                                           the token
     * @throws InvalidWsIdentifierTokenException if the identifier type in the request is invalid or
     *                                           cannot be processed
     */
    public WsExchangeTokenResponse exchangeToken(final String callerOIN,
                                                 final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest)
        throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException, InvalidCipherTextException {

        final String encodedToken = aesGcmCryptographerService.decrypt(
            wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);

        final Token token = tokenConverter.deSerialize(encodedToken);

        if (!oinValidator.isValid(callerOIN, token)) {
            throw new RuntimeException("CallerOIN and token are mismatched.");
        }

        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                return bsnTokenMapper.map(token);
            }
            case ORGANISATION_PSEUDO -> {
                return organisationPseudoTokenMapper.map(callerOIN, token);
            }
            default -> throw new RuntimeException(
                "Invalid identifier cannot be processed.");
        }
    }
}
