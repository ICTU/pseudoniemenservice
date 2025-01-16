package nl.appsource.model.v1;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Identifier {

    private static final String VERSION = "V1";

    private String version;
    private String bsn;

    public static Identifier fromBsn(final String bsn) {

        final Identifier identifier = new Identifier();

        identifier.setVersion(VERSION);
        identifier.setBsn(bsn);

        return identifier;
    }
}
