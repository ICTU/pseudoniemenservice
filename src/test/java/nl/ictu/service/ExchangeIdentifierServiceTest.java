package nl.ictu.service;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.exception.InvalidWsIdentifierRequestTypeException;
import nl.ictu.service.map.BsnPseudoMapper;
import nl.ictu.service.map.PseudoBsnMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ExchangeIdentifierService}.
 */
@ExtendWith(MockitoExtension.class)
class ExchangeIdentifierServiceTest {

    @Mock
    private BsnPseudoMapper bsnPseudoMapper;
    @Mock
    private PseudoBsnMapper pseudoBsnMapper;
    @InjectMocks
    private ExchangeIdentifierService exchangeIdentifierService;

    @Test
    @DisplayName("""
            Given a BSN identifier and recipientIdentifierType ORGANISATION_PSEUDO
            When exchangeIdentifier() is called
            Then it should return a response with ORGANISATION_PSEUDO type and encrypted value
            """)
    void testExchangeIdentifier_BsnToOrgPseudo() throws Exception {
        // GIVEN
        var request = WsExchangeIdentifierRequest.builder()
                .identifier(WsIdentifier.builder()
                        .type(BSN)
                        .value("123456789")
                        .build())
                .recipientOIN("TEST_OIN")
                .recipientIdentifierType(ORGANISATION_PSEUDO)
                .build();
        // We mock BsnPseudoMapper to return a WsExchangeIdentifierResponse
        final var mockedResponse = WsExchangeIdentifierResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(ORGANISATION_PSEUDO)
                        .value("encryptedValue")
                        .build())
                .build();
        when(bsnPseudoMapper.map("123456789", "TEST_OIN")).thenReturn(mockedResponse);
        // WHEN
        WsExchangeIdentifierResponse actualResponse =
                exchangeIdentifierService.exchangeIdentifier(request);
        // THEN
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getIdentifier());
        assertEquals(ORGANISATION_PSEUDO,
                actualResponse.getIdentifier().getType());
        assertEquals("encryptedValue",
                actualResponse.getIdentifier().getValue());
    }

    @Test
    @DisplayName("""
            Given an ORGANISATION_PSEUDO identifier and recipientIdentifierType BSN
            When exchangeIdentifier() is called
            Then it should return a response with BSN type and decrypted value
            """)
    void testExchangeIdentifier_OrgPseudoToBsn() throws Exception {
        // GIVEN
        final var request = WsExchangeIdentifierRequest.builder()
                .identifier(WsIdentifier.builder()
                        .type(ORGANISATION_PSEUDO)
                        .value("somePseudo")
                        .build())
                .recipientOIN("TEST_OIN")
                .recipientIdentifierType(BSN)
                .build();
        // We mock PseudoBsnMapper to return a WsExchangeIdentifierResponse
        final var mockedResponse = WsExchangeIdentifierResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(BSN)
                        .value("decryptedBsn")
                        .build())
                .build();
        when(pseudoBsnMapper.map("somePseudo", "TEST_OIN")).thenReturn(mockedResponse);
        // WHEN
        WsExchangeIdentifierResponse actualResponse =
                exchangeIdentifierService.exchangeIdentifier(request);
        // THEN
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getIdentifier());
        assertEquals(BSN,
                actualResponse.getIdentifier().getType());
        assertEquals("decryptedBsn",
                actualResponse.getIdentifier().getValue());
    }

    @Test
    @DisplayName("""
            Given a request with unsupported identifier mapping (BSN -> BSN or ORG_PSEUDO -> ORG_PSEUDO)
            When exchangeIdentifier() is called
            Then it should throw InvalidWsIdentifierRequestTypeException
            """)
    void testExchangeIdentifier_UnsupportedMapping_ThrowsException() {
        // GIVEN
        final var request = WsExchangeIdentifierRequest.builder()
                // Let's say we do something like BSN -> BSN or ORG_PSEUDO -> ORG_PSEUDO
                .identifier(WsIdentifier.builder()
                        .type(BSN)
                        .value("12345")
                        .build())
                .recipientOIN("TEST_OIN")
                .recipientIdentifierType(BSN)
                .build();
        // WHEN & THEN
        assertThrows(
                InvalidWsIdentifierRequestTypeException.class,
                () -> exchangeIdentifierService.exchangeIdentifier(request)
        );
    }
}
