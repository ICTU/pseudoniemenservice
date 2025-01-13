package nl.appsource.service;

import lombok.RequiredArgsConstructor;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.appsource.service.map.BsnPseudoMapper;
import nl.appsource.service.map.PseudoBsnMapper;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

@RequiredArgsConstructor
@Service
public final class ExchangeIdentifierService {

    private final BsnPseudoMapper bsnPseudoMapper;
    private final PseudoBsnMapper pseudoBsnMapper;

    /**
     * Processes the exchange of an identifier between different types based on specific mappings
     * and returns the corresponding response.
     * caller.
     *
     * @param wsExchangeIdentifierForIdentifierRequest The request object containing details of the
     *                                                 identifier to be exchanged, including its
     *                                                 value, type, recipient OIN, and recipient
     *                                                 identifier type.
     * @return A {@link WsExchangeIdentifierResponse} containing the exchanged identifier. Returns
     * null if no appropriate mapping exists for the provided inputs.
     */
    public WsExchangeIdentifierResponse exchangeIdentifier(
        final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) throws InvalidCipherTextException, IOException {

        final WsIdentifier wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();
        final String recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();
        final WsIdentifierTypes recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();

        if (BSN.equals(wsIdentifierRequest.getType()) && ORGANISATION_PSEUDO.equals(
            recipientIdentifierType)) {
            // BSN to ORG_PSEUDO
            return bsnPseudoMapper.map(wsIdentifierRequest.getValue(), recipientOIN);
        } else if (ORGANISATION_PSEUDO.equals(wsIdentifierRequest.getType()) && BSN.equals(
            recipientIdentifierType)) {
            // ORG_PSEUDO to BSN
            return pseudoBsnMapper.map(wsIdentifierRequest.getValue(), recipientOIN);
        } else {
            throw new RuntimeException("Unsupported types for convertion");
        }
    }
}
