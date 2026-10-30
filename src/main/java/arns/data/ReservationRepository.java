package arns.data;

import arns.model.*;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public final class ReservationRepository {
  private final Database database;

  public ReservationRepository(Database database, List<CatalogItem> catalog) throws SQLException {
    this.database = database;
    try (Connection c = database.connect()) {
      c.setAutoCommit(false);
      try (PreparedStatement s =
          c.prepareStatement(
              "MERGE INTO"
                  + " catalog_items(id,category,title,city,description,unit_price_cents,currency)"
                  + " KEY(id) VALUES(?,?,?,?,?,?,'CAD')")) {
        for (var item : catalog) {
          s.setString(1, item.id());
          s.setString(2, item.category().name());
          s.setString(3, item.title());
          s.setString(4, item.city());
          s.setString(5, item.description());
          s.setLong(6, item.price().cents());
          s.addBatch();
        }
        s.executeBatch();
        c.commit();
      } catch (SQLException e) {
        c.rollback();
        throw e;
      }
    }
  }

  private static final String SELECT =
      "SELECT r.*,c.full_name,c.email,i.category,i.title,i.city,i.description,i.unit_price_cents AS"
          + " catalog_price FROM reservations r JOIN contacts c ON c.id=r.contact_id JOIN"
          + " catalog_items i ON i.id=r.item_id";

  public List<Reservation> list() throws SQLException {
    try (Connection c = database.connect();
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery(SELECT + " ORDER BY r.created_at DESC,r.id")) {
      List<Reservation> result = new ArrayList<>();
      while (r.next()) result.add(read(r));
      return List.copyOf(result);
    }
  }

  private Reservation read(ResultSet r) throws SQLException {
    var item =
        new CatalogItem(
            r.getString("item_id"),
            Category.valueOf(r.getString("category")),
            r.getString("title"),
            r.getString("city"),
            r.getString("description"),
            new Money(r.getLong("catalog_price")));
    return new Reservation(
        r.getObject("id", UUID.class),
        item,
        r.getString("full_name"),
        r.getString("email"),
        r.getObject("travel_date", LocalDate.class),
        r.getInt("units"),
        new Money(r.getLong("unit_price_cents")),
        Reservation.Status.valueOf(r.getString("status")),
        r.getInt("version"),
        r.getObject("created_at", java.time.OffsetDateTime.class).toInstant());
  }

  public Reservation find(UUID id) throws SQLException {
    try (Connection c = database.connect();
        PreparedStatement s = c.prepareStatement(SELECT + " WHERE r.id=?")) {
      s.setObject(1, id);
      try (ResultSet r = s.executeQuery()) {
        if (!r.next())
          throw new IllegalArgumentException("Reservation no longer exists. Refresh the plan.");
        return read(r);
      }
    }
  }

  private UUID contact(Connection c, String name, String email) throws SQLException {
    try (PreparedStatement s =
        c.prepareStatement("SELECT id FROM contacts WHERE full_name=? AND email=?")) {
      s.setString(1, name);
      s.setString(2, email);
      try (ResultSet r = s.executeQuery()) {
        if (r.next()) return r.getObject(1, UUID.class);
      }
    }
    UUID id = UUID.randomUUID();
    try (PreparedStatement s = c.prepareStatement("INSERT INTO contacts VALUES(?,?,?)")) {
      s.setObject(1, id);
      s.setString(2, name);
      s.setString(3, email);
      s.executeUpdate();
    }
    return id;
  }

  public Reservation insert(
      CatalogItem item, String name, String email, LocalDate date, int units, Instant now)
      throws SQLException {
    UUID id = UUID.randomUUID();
    try (Connection c = database.connect()) {
      c.setAutoCommit(false);
      try {
        UUID contact = contact(c, name, email);
        try (PreparedStatement s =
            c.prepareStatement("INSERT INTO reservations VALUES(?,?,?,?,?,?,'DRAFT',1,?)")) {
          s.setObject(1, id);
          s.setString(2, item.id());
          s.setObject(3, contact);
          s.setObject(4, date);
          s.setInt(5, units);
          s.setLong(6, item.price().cents());
          s.setObject(7, now.atOffset(java.time.ZoneOffset.UTC));
          s.executeUpdate();
        }
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
    return find(id);
  }

  public Reservation update(
      UUID id, int version, String name, String email, LocalDate date, int units)
      throws SQLException {
    try (Connection c = database.connect()) {
      c.setAutoCommit(false);
      try {
        UUID contact = contact(c, name, email);
        try (PreparedStatement s =
            c.prepareStatement(
                "UPDATE reservations SET contact_id=?,travel_date=?,units=?,version=version+1 WHERE"
                    + " id=? AND version=? AND status='DRAFT'")) {
          s.setObject(1, contact);
          s.setObject(2, date);
          s.setInt(3, units);
          s.setObject(4, id);
          s.setInt(5, version);
          changed(s.executeUpdate());
        }
        cleanContacts(c);
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
    return find(id);
  }

  public void delete(UUID id, int version) throws SQLException {
    try (Connection c = database.connect()) {
      c.setAutoCommit(false);
      try (PreparedStatement s =
          c.prepareStatement(
              "DELETE FROM reservations WHERE id=? AND version=? AND status='DRAFT'")) {
        s.setObject(1, id);
        s.setInt(2, version);
        changed(s.executeUpdate());
        cleanContacts(c);
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
  }

  /** All requested state changes succeed together, or all are rolled back. */
  public void transition(
      Map<UUID, Integer> versions, Reservation.Status from, Reservation.Status to)
      throws SQLException {
    if (!((from == Reservation.Status.DRAFT && to == Reservation.Status.REQUESTED)
        || (from == Reservation.Status.REQUESTED && to == Reservation.Status.CANCELLED)))
      throw new IllegalArgumentException("Unsupported reservation transition.");
    if (versions.isEmpty()) throw new IllegalArgumentException("Select at least one reservation.");
    try (Connection c = database.connect()) {
      c.setAutoCommit(false);
      try (PreparedStatement s =
          c.prepareStatement(
              "UPDATE reservations SET status=?,version=version+1 WHERE id=? AND version=? AND"
                  + " status=?")) {
        for (var entry : versions.entrySet()) {
          s.setString(1, to.name());
          s.setObject(2, entry.getKey());
          s.setInt(3, entry.getValue());
          s.setString(4, from.name());
          changed(s.executeUpdate());
        }
        c.commit();
      } catch (SQLException | RuntimeException e) {
        c.rollback();
        throw e;
      }
    }
  }

  private static void changed(int count) {
    if (count != 1)
      throw new IllegalStateException(
          "The reservation changed or is no longer editable. Refresh and try again.");
  }

  private void cleanContacts(Connection c) throws SQLException {
    try (Statement s = c.createStatement()) {
      s.executeUpdate("DELETE FROM contacts WHERE id NOT IN (SELECT contact_id FROM reservations)");
    }
  }
}
