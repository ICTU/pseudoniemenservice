package nl.appsource.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.model.Identifier;
import nl.appsource.model.Token;
import nl.appsource.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.service.crypto.AesGcmCryptographerService;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import nl.appsource.service.crypto.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static nl.appsource.service.map.WsGetTokenResponseMapper.V_1;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenController implements ExchangeTokenApi, VersionOneController {

    private final AesGcmCryptographerService aesGcmCryptographerService;

    private final TokenConverter tokenConverter;

    private final AesGcmSivCryptographerService aesGcmSivCryptographerService;

    /**
     * Handles the exchange of a token and returns the corresponding identifier in a response. This
     * method validates the caller's OIN, processes the incoming token using the specified
     * identifier type, and constructs a response accordingly.
     *
     * @param callerOIN                           The identifier of the requesting organization
     *                                            (OIN).
     * @param wsExchangeTokenForIdentifierRequest The request containing the token and identifier
     *                                            type details.
     * @return A response entity containing the converted identifier or a status indicating failure.
     */
    @Override
    public ResponseEntity<WsExchangeTokenResponse> exchangeToken(final String callerOIN, final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {
        try {

            // lookup caller

            // caller authorisation

            // decrypt token

            final String serializedToken = aesGcmCryptographerService.decrypt(wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);

            // deserialize token

            final Token token = tokenConverter.deSerialize(serializedToken);

            // validate token

            if (!Objects.equals(callerOIN, token.getRecipientOIN())) {
                throw new RuntimeException("CallerOIN and token mismatch");
            }

            // create response

            final WsExchangeTokenResponse.WsExchangeTokenResponseBuilder wsExchangeTokenResponseBuilder = WsExchangeTokenResponse.builder();

            final WsIdentifier.WsIdentifierBuilder wsIdentifierBuilder = WsIdentifier.builder();

            switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {

                // no convesion
                case BSN -> wsIdentifierBuilder.type(BSN).value(token.getBsn());

                // BSN -> ORHANISATION_PSEUDO conversion
                case ORGANISATION_PSEUDO -> {

                    final Identifier tokenIdentifier = Identifier.builder()
                        .version(V_1)
                        .bsn(token.getBsn())
                        .build();

                    final String encryptedIdentifier = aesGcmSivCryptographerService.encryptIdentifier(tokenIdentifier, callerOIN);

                    wsIdentifierBuilder.type(ORGANISATION_PSEUDO).value(encryptedIdentifier);
                }

                default -> throw new RuntimeException("Unknown identifier type");

            }

            wsExchangeTokenResponseBuilder.identifier(wsIdentifierBuilder.build());

            return ResponseEntity.ok(wsExchangeTokenResponseBuilder.build());
        } catch (final Exception e) {
            log.error("", e);
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
