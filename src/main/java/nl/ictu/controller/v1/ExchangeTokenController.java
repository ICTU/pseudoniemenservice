package nl.ictu.controller.v1;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.Identifier;
import nl.ictu.controller.exception.InvalidOINException;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.v1.crypto.AesGcmCryptographer;
import nl.ictu.service.v1.crypto.AesGcmSivCryptographer;
import nl.ictu.service.v1.crypto.TokenConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenController implements ExchangeTokenApi, VersionOneController {

    private final AesGcmCryptographer aesGcmCryptographer;
    private final AesGcmSivCryptographer aesGcmSivCryptographer;
    private final TokenConverter tokenConverter;

    @Override
    @SneakyThrows
    public ResponseEntity<WsExchangeTokenResponse> exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        final var encodedToken = aesGcmCryptographer.decrypt(wsExchangeTokenForIdentifierRequest.getToken(), callerOIN);
        final var token = tokenConverter.decode(encodedToken);
        if (!callerOIN.equals(token.getRecipientOIN())) {
            throw new InvalidOINException("Sink OIN not the same");
        }
        final var wsExchangeTokenForIdentifier200Response = new WsExchangeTokenResponse();
        final var wsIdentifier = new WsIdentifier();
        switch (wsExchangeTokenForIdentifierRequest.getIdentifierType()) {
            case BSN -> {
                wsIdentifier.setType(BSN);
                wsIdentifier.setValue(token.getBsn());
            }
            case ORGANISATION_PSEUDO -> {
                final Identifier identifier = new Identifier();
                identifier.setBsn(token.getBsn());
                final String encrypt = aesGcmSivCryptographer.encrypt(identifier, callerOIN);
                wsIdentifier.setType(ORGANISATION_PSEUDO);
                wsIdentifier.setValue(encrypt);
            }
            default -> {
                return ResponseEntity.status(UNPROCESSABLE_ENTITY).build();
            }
        }
        wsExchangeTokenForIdentifier200Response.setIdentifier(wsIdentifier);
        return ResponseEntity.ok(wsExchangeTokenForIdentifier200Response);
    }
}
