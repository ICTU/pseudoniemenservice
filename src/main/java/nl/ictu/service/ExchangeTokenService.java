package nl.appsource.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.appsource.service.crypto.AesGcmCryptographerService;
import nl.appsource.service.crypto.TokenConverter;
import nl.appsource.service.exception.InvalidOINException;
import nl.appsource.service.exception.InvalidWsIdentifierTokenException;
import nl.appsource.service.map.BsnTokenMapper;
import nl.appsource.service.map.OrganisationPseudoTokenMapper;
import nl.appsource.service.validate.OINValidator;
import org.springframework.web.bind.annotation.RestController;

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
    @SneakyThrows
    public WsExchangeTokenResponse exchangeToken(final String callerOIN,
                                                 final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        final var encodedToken = aesGcmCryptographerService.decrypt(
            wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);
        final var token = tokenConverter.decode(encodedToken);
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
