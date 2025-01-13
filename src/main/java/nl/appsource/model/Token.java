package nl.appsource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Token {

    private String version;
    private String bsn;
    private String recipientOIN;
    private Long creationDate;

}
