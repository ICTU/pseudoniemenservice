package nl.ictu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Token {

    private String version;
    private String bsn;
    private String recipientOIN;
    private Long creationDate;

}
