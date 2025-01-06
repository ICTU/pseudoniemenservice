package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WsIdentifierOinBsnMapperTest {

    @Mock
    private EncryptedBsnMapper encryptedBsnMapper;

    @InjectMocks
    private WsIdentifierOinBsnMapper wsIdentifierOinBsnMapper;

    @Test
    @DisplayName("""
            Given a WsIdentifier of type BSN with a BSN value
            When the map() method is called
            Then the BSN value should be returned directly
            """)
    void testMap_BsnType() {
        // GIVEN
        final var bsnValue = "987654321";
        final var identifier = new WsIdentifier().type(BSN).value(bsnValue);
        // WHEN
        final var result = wsIdentifierOinBsnMapper.map(identifier, "123456789");
        // THEN
        assertEquals(bsnValue, result);
        verifyNoInteractions(encryptedBsnMapper); // Ensure EncryptedBsnMapper is not called
    }

    @Test
    @DisplayName("""
            Given a WsIdentifier of type ORGANISATION_PSEUDO with a BSN value
            When the map() method is called
            Then the encrypted value should be returned
            """)
    void testMap_OrganisationPseudoType() {
        // GIVEN
        final var bsnValue = "987654321";
        final var recipientOIN = "123456789";
        final var encryptedValue = "encrypted-value";
        final var identifier = new WsIdentifier()
                .type(ORGANISATION_PSEUDO)
                .value(bsnValue);
        when(encryptedBsnMapper.map(bsnValue, recipientOIN)).thenReturn(encryptedValue);
        // WHEN
        String result = wsIdentifierOinBsnMapper.map(identifier, recipientOIN);
        // THEN
        assertEquals(encryptedValue, result);
        verify(encryptedBsnMapper).map(bsnValue, recipientOIN); // Ensure EncryptedBsnMapper is called
    }
}
