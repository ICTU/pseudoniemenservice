package nl.ictu.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import lombok.RequiredArgsConstructor;
import nl.ictu.model.Identifier;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@RegisterReflectionForBinding({Identifier.class})
public class IdentifierConverter {

    private final ObjectMapper objectMapper;

    /**
     * Encodes the given Identifier object into its JSON representation as a string.
     *
     * @param identifier the Identifier object to be encoded
     * @return the JSON string representation of the given Identifier object
     * @throws IOException if an I/O error occurs during encoding
     */
    public String encode(final Identifier identifier) throws IOException {

        final StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, identifier);
        return stringWriter.toString();
    }

    /**
     * Decodes a JSON string into an Identifier object.
     *
     * @param encodedIdentifier the JSON string representation of an Identifier object
     * @return the deserialized Identifier object
     * @throws JsonProcessingException if an error occurs while processing the JSON string
     */
    public Identifier decode(final String encodedIdentifier) throws JsonProcessingException {

        return objectMapper.readValue(encodedIdentifier, Identifier.class);
    }
}
