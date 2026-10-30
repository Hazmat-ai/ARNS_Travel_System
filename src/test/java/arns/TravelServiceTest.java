package arns;

import static org.junit.jupiter.api.Assertions.*;

import arns.data.*;
import arns.model.*;
import arns.service.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class TravelServiceTest {
  @TempDir Path directory;
  Database db;
  CatalogService catalog;
  ReservationRepository repository;
  TravelService service;
  final Clock clock = Clock.fixed(Instant.parse("2026-10-30T12:00:00Z"), ZoneOffset.UTC);
  final LocalDate travel = LocalDate.of(2026, 11, 15);

  @BeforeEach
  void start() throws Exception {
    db = Database.memory();
    catalog = new CatalogService();
    repository = new ReservationRepository(db, catalog.all());
    service = new TravelService(catalog, repository, clock);
  }

  @AfterEach
  void close() throws Exception {
    db.close();
  }

  CatalogItem hotel() {
    return catalog.search("Paris", Category.HOTELS, "", false).get(0);
  }

  Reservation add(String name) throws Exception {
    return service.add(hotel().id(), name, "alex@example.com", travel, 3);
  }

  int contacts() throws Exception {
    try (Connection c = db.connect();
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("SELECT count(*) FROM contacts")) {
      r.next();
      return r.getInt(1);
    }
  }

  @Test
  void moneyUsesExactMinorUnits() {
    assertEquals(30, Money.of("0.10").multiply(3).cents());
    assertEquals("CAD 12.30", Money.of("12.30").display());
    assertThrows(ArithmeticException.class, () -> Money.of("1.001"));
    assertThrows(IllegalArgumentException.class, () -> new Money(-1));
    assertThrows(IllegalArgumentException.class, () -> new Money(1).multiply(0));
  }

  @Test
  void catalogIsTypedSearchableAndClearlyDescribed() {
    assertTrue(catalog.all().size() > 400);
    assertEquals(1, catalog.search("Paris", Category.FLIGHTS, "Toronto", false).size());
    assertTrue(catalog.search("Paris", Category.HOTELS, "not-a-real-option", false).isEmpty());
    assertTrue(
        catalog.search("Paris", Category.HOTELS, "", true).get(0).price().compareTo(hotel().price())
            <= 0);
    assertThrows(UnsupportedOperationException.class, () -> catalog.all().clear());
  }

  @Test
  void quantityAndContactPersistWithoutParsingDisplayText() throws Exception {
    var r = add("Alex Doe");
    assertEquals(hotel().price().cents() * 3, r.total().cents());
    assertEquals(Reservation.Status.DRAFT, r.status());
    assertEquals(r, repository.find(r.id()));
    assertEquals(r.total(), TravelService.total(service.reservations()));
  }

  @Test
  void invalidInputsNeverCreateRows() throws Exception {
    assertThrows(
        IllegalArgumentException.class, () -> service.add(hotel().id(), "", "bad", travel, 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> service.add(hotel().id(), "Alex", "not-mail", travel, 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> service.add(hotel().id(), "Alex", "a@b.ca", LocalDate.of(2020, 1, 1), 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> service.add(hotel().id(), "Alex", "a@b.ca", service.today().plusYears(4), 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> service.add(hotel().id(), "Alex\nDoe", "a@b.ca", travel, 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> service.add(hotel().id(), "Alex", "a@b.ca", travel, 366));
    assertTrue(service.reservations().isEmpty());
    assertEquals(0, contacts());
  }

  @Test
  void editsKeepPriceSnapshotsAndReplaceContacts() throws Exception {
    var r = add("First Person");
    var edited = service.edit(r, "Second Person", "second@example.com", travel.plusDays(1), 5);
    assertEquals(2, edited.version());
    assertEquals(r.unitPrice(), edited.unitPrice());
    assertEquals(5, edited.units());
    assertEquals(1, contacts());
  }

  @Test
  void staleEditsAreRejectedAndNewContactInsertionRollsBack() throws Exception {
    var original = add("Alex");
    service.edit(original, "Alex Updated", "updated@example.com", travel, 2);
    assertThrows(
        IllegalStateException.class,
        () -> service.edit(original, "Stale", "stale@example.com", travel, 1));
    assertEquals(1, contacts());
    assertEquals("Alex Updated", repository.find(original.id()).traveller());
  }

  @Test
  void followUpIsDurableAndNotEditableOrDeletable() throws Exception {
    var r = add("Alex");
    service.request(List.of(r));
    var requested = repository.find(r.id());
    assertEquals(Reservation.Status.REQUESTED, requested.status());
    assertThrows(
        IllegalStateException.class,
        () -> service.edit(requested, "Edit", "edit@example.com", travel, 2));
    assertThrows(IllegalStateException.class, () -> service.remove(requested));
    service.cancel(List.of(requested));
    assertEquals(Reservation.Status.CANCELLED, repository.find(r.id()).status());
    assertEquals(0, TravelService.total(service.reservations()).cents());
  }

  @Test
  void invalidTransitionsAndDuplicatesAreRejected() throws Exception {
    var r = add("Alex");
    assertThrows(IllegalStateException.class, () -> service.cancel(List.of(r)));
    assertThrows(IllegalArgumentException.class, () -> service.request(List.of(r, r)));
    assertThrows(IllegalArgumentException.class, () -> service.request(List.of()));
    assertEquals(Reservation.Status.DRAFT, repository.find(r.id()).status());
  }

  @Test
  void batchRequestsRollBackIfAnyVersionIsStale() throws Exception {
    var first = add("First");
    var second = add("Second");
    service.edit(second, "Second", "alex@example.com", travel, 4);
    assertThrows(IllegalStateException.class, () -> service.request(List.of(first, second)));
    assertEquals(Reservation.Status.DRAFT, repository.find(first.id()).status());
    assertEquals(Reservation.Status.DRAFT, repository.find(second.id()).status());
  }

  @Test
  void removeUsesIdentityAndCleansOrphanContacts() throws Exception {
    var first = add("First");
    var second = add("Second");
    service.remove(first);
    assertEquals(List.of(second), service.reservations());
    assertEquals(1, contacts());
    assertThrows(IllegalStateException.class, () -> service.remove(first));
  }

  @Test
  void csvEscapesCommasQuotesAndSpreadsheetFormulas() throws Exception {
    var r = add("=HYPERLINK(\"bad\")");
    Path export = directory.resolve("plan.csv");
    CsvExport.write(export, List.of(r));
    String text = Files.readString(export);
    assertTrue(text.contains("\"'=HYPERLINK(\"\"bad\"\")\""));
    assertTrue(text.contains("total_cad"));
    assertTrue(text.contains(r.total().decimal().toPlainString()));
    assertThrows(java.io.IOException.class, () -> CsvExport.write(export, List.of(r)));
    assertEquals("\"'+name\"", CsvExport.cell("+name"));
  }

  @Test
  void persistenceSurvivesReopening() throws Exception {
    Path folder = directory.resolve("store");
    UUID id;
    try (Database file = new Database(folder)) {
      var repo = new ReservationRepository(file, catalog.all());
      var api = new TravelService(catalog, repo, clock);
      id = api.add(hotel().id(), "Persisted", "local@example.com", travel, 2).id();
    }
    try (Database reopened = new Database(folder)) {
      var repo = new ReservationRepository(reopened, catalog.all());
      assertEquals("Persisted", repo.find(id).traveller());
    }
  }

  @Test
  void backupCanRestoreAnOpenDatabase() throws Exception {
    Path folder = directory.resolve("live"),
        backup = directory.resolve("backup.zip"),
        restore = directory.resolve("restore");
    try (Database file = new Database(folder)) {
      var repo = new ReservationRepository(file, catalog.all());
      var api = new TravelService(catalog, repo, clock);
      var r = api.add(hotel().id(), "Backed Up", "backup@example.com", travel, 1);
      file.backup(backup);
      try (ZipFile zip = new ZipFile(backup.toFile())) {
        assertNotNull(zip.getEntry("travel.mv.db"));
      }
      assertThrows(java.io.IOException.class, () -> file.backup(backup));
      org.h2.tools.Restore.execute(backup.toString(), restore.toString(), "travel");
      try (Database restored = new Database(restore)) {
        assertEquals(r.id(), new ReservationRepository(restored, catalog.all()).list().get(0).id());
      }
    }
  }

  @Test
  void sqlValuesAreParametersAndSchemaRejectsInvalidQuantities() throws Exception {
    var r = add("Robert'); DROP TABLE reservations;--");
    assertEquals(1, service.reservations().size());
    try (Connection c = db.connect();
        PreparedStatement s = c.prepareStatement("UPDATE reservations SET units=0 WHERE id=?")) {
      s.setObject(1, r.id());
      assertThrows(SQLException.class, s::executeUpdate);
    }
    assertEquals(3, repository.find(r.id()).units());
  }
}
