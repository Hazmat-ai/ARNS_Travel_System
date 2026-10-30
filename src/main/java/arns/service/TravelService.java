package arns.service;

import arns.data.ReservationRepository;
import arns.model.*;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

/** Public application boundary; the UI never writes SQL or manipulates private state. */
public final class TravelService {
  private final CatalogService catalog;
  private final ReservationRepository repository;
  private final Clock clock;

  public TravelService(CatalogService catalog, ReservationRepository repository, Clock clock) {
    this.catalog = catalog;
    this.repository = repository;
    this.clock = clock;
  }

  public CatalogService catalog() {
    return catalog;
  }

  public LocalDate today() {
    return LocalDate.now(clock);
  }

  public List<Reservation> reservations() throws SQLException {
    return repository.list();
  }

  public Reservation add(String itemId, String name, String email, LocalDate date, int units)
      throws SQLException {
    var item = catalog.find(itemId);
    String person = Checks.text(name, "Traveller name", 120), address = Checks.email(email);
    Checks.date(date, today());
    Checks.units(units);
    item.price().multiply(units);
    return repository.insert(item, person, address, date, units, Instant.now(clock));
  }

  public Reservation edit(Reservation current, String name, String email, LocalDate date, int units)
      throws SQLException {
    var actual = repository.find(current.id());
    actual.unitPrice().multiply(Checks.units(units));
    return repository.update(
        current.id(),
        current.version(),
        Checks.text(name, "Traveller name", 120),
        Checks.email(email),
        Checks.date(date, today()),
        units);
  }

  public void remove(Reservation current) throws SQLException {
    repository.delete(current.id(), current.version());
  }

  public void request(List<Reservation> selected) throws SQLException {
    for (var r : selected) Checks.date(repository.find(r.id()).date(), today());
    repository.transition(
        versions(selected), Reservation.Status.DRAFT, Reservation.Status.REQUESTED);
  }

  public void cancel(List<Reservation> selected) throws SQLException {
    repository.transition(
        versions(selected), Reservation.Status.REQUESTED, Reservation.Status.CANCELLED);
  }

  private Map<UUID, Integer> versions(List<Reservation> selected) {
    Map<UUID, Integer> result = new LinkedHashMap<>();
    for (var r : selected)
      if (result.put(r.id(), r.version()) != null)
        throw new IllegalArgumentException("Duplicate reservation selection.");
    return result;
  }

  public static Money total(List<Reservation> rows) {
    long total = 0;
    for (var r : rows)
      if (r.status() != Reservation.Status.CANCELLED)
        total = Math.addExact(total, r.total().cents());
    return new Money(total);
  }
}
