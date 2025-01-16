package nl.appsource.persistence;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrganisatieRepository {

    private static List<Organisation> organisations = new ArrayList<>();

    static {
        organisations = List.of(
            new Organisation("OrganisationA", "00000008855800191020", UUID.fromString("308f9335-6d2d-4c15-be43-f3da17ec2c5f"), "nM88Kus0R1qhgvAFpW784pajM5ufJ7s0"),
            new Organisation("OrganisationB", "00000000123450112345", UUID.fromString("62bafb36-31d2-4d87-8493-216efea45630"), "HCptRD7W81E9RWYS6tOemz8R5lQtJTNP"),
            new Organisation("OrganisationC", "3", UUID.fromString("036aaf74-765a-4fd0-afac-ccb282841ca0"), "gYVh6638lnkY0a3dPKCi0Q4G5JMTUVjc")
        );
    }

    /**
     * Find an Orga by Oin.
     * @param oin the Oin
     * @return the Optional organisation.
     */

    public Optional<Organisation> findByOin(final String oin) {
        return organisations.stream().filter(o -> Objects.equals(oin, o.oin())).findFirst();
    }

}
