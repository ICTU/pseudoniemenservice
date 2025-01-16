package nl.appsource.persistence;

import java.util.UUID;

public record Organisation(String name, String oin, UUID id, String pseudoSecret) {
}
