package nl.ictu.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.service.exception.InvalidOINException;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.service.exception.InvalidWsIdentifierTokenException;
import nl.ictu.crypto.AesGcmCryptographer;
import nl.ictu.crypto.TokenCoder;
import nl.ictu.service.map.BsnTokenMapper;
import nl.ictu.service.map.OrganisationPseudoTokenMapper;
import nl.ictu.service.validate.OINValidator;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenService {

    private final AesGcmCryptographer aesGcmCryptographer;
    private final TokenCoder tokenCoder;
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
    @SneakyThrows
    public WsExchangeTokenResponse exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        final var encodedToken = aesGcmCryptographer.decrypt(
                wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);
        final var token = tokenCoder.decode(encodedToken);
        if (!oinValidator.isValid(callerOIN, token)) {
            throw new InvalidOINException("CallerOIN and token are mismatched.");
        }
        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                return bsnTokenMapper.map(token);
            }
            case ORGANISATION_PSEUDO -> {
                return organisationPseudoTokenMapper.map(callerOIN, token);
            }
            default -> throw new InvalidWsIdentifierTokenException(
                    "Invalid identifier cannot be processed.");
        }
    }
}
