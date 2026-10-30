package arns.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record Reservation(
    UUID id,
    CatalogItem item,
    String traveller,
    String email,
    LocalDate date,
    int units,
    Money unitPrice,
    Status status,
    int version,
    Instant createdAt) {
  public enum Status {
    DRAFT,
    REQUESTED,
    CANCELLED
  }

  public Reservation {
    Objects.requireNonNull(id);
    Objects.requireNonNull(item);
    Objects.requireNonNull(date);
    Objects.requireNonNull(unitPrice);
    Objects.requireNonNull(status);
    Objects.requireNonNull(createdAt);
    traveller = Checks.text(traveller, "Traveller name", 120);
    email = Checks.email(email);
    Checks.units(units);
    if (version < 1) throw new IllegalArgumentException("Invalid reservation version.");
    unitPrice.multiply(units);
  }

  public Money total() {
    return unitPrice.multiply(units);
  }
}
