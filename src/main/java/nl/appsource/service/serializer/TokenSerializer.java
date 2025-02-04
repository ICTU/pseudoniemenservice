package nl.appsource.service.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nl.appsource.model.v1.Token;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

@Component
@RequiredArgsConstructor
@RegisterReflectionForBinding({Token.class})
public class TokenSerializer {

    private final ObjectMapper objectMapper;

    /**
     * Encodes the given Token object to its JSON string representation.
     *
     * @param token the Token object to be encoded
     * @return the JSON string representation of the given Token object
     * @throws IOException if an I/O error occurs during encoding
     */
    public String serialize(final Token token) throws IOException {

        final StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, token);
        return stringWriter.toString();
    }

    /**
     * Decodes a JSON-encoded token string into a Token object.
     *
     * @param encodedToken the JSON string representation of a Token
     * @return the decoded Token object
     * @throws JsonProcessingException if the JSON string cannot be parsed into a Token object
     */
    public Token deSerialize(final String encodedToken) throws JsonProcessingException {

        return objectMapper.readValue(encodedToken, Token.class);
    }
}
