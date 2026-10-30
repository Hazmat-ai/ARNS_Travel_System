package arns.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.*;
import java.util.Objects;

/** Embedded local store. No TCP listener or web console is enabled. */
public final class Database implements AutoCloseable {
  private final String url;
  private final Connection keeper;

  public Database(Path directory) throws SQLException, IOException {
    Path root = directory.toAbsolutePath().normalize();
    if (root.toString().matches("(?s).*[;\\r\\n].*"))
      throw new IllegalArgumentException("Data path contains unsupported characters.");
    Files.createDirectories(root);
    restrict(root, true);
    url = "jdbc:h2:file:" + root.resolve("travel") + ";DB_CLOSE_ON_EXIT=FALSE;LOCK_TIMEOUT=5000";
    keeper = connect();
    try {
      migrate();
      restrict(root.resolve("travel.mv.db"), false);
    } catch (SQLException | IOException | RuntimeException e) {
      keeper.close();
      throw e;
    }
  }

  private Database(String name) throws SQLException, IOException {
    url = "jdbc:h2:mem:" + name + ";DB_CLOSE_ON_EXIT=FALSE;LOCK_TIMEOUT=5000";
    keeper = connect();
    migrate();
  }

  public static Database memory() throws SQLException, IOException {
    return new Database(java.util.UUID.randomUUID().toString());
  }

  public Connection connect() throws SQLException {
    return DriverManager.getConnection(url, "arns", "");
  }

  private void migrate() throws SQLException, IOException {
    try (Statement s = keeper.createStatement()) {
      boolean hasVersion;
      try (ResultSet r =
          keeper.getMetaData().getTables(null, null, "SCHEMA_VERSION", new String[] {"TABLE"})) {
        hasVersion = r.next();
      }
      if (hasVersion)
        try (ResultSet r = s.executeQuery("SELECT version FROM schema_version")) {
          while (r.next())
            if (r.getInt(1) != 1)
              throw new SQLException(
                  "Unsupported database schema. Use a compatible application version.");
        }
      String schema;
      try (var stream = Objects.requireNonNull(getClass().getResourceAsStream("/schema.sql"))) {
        schema = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
      }
      for (String sql : schema.split(";")) if (!sql.isBlank()) s.execute(sql);
      s.executeUpdate("MERGE INTO schema_version KEY(version) VALUES(1)");
    }
  }

  public static void restrict(Path path, boolean directory) throws IOException {
    try {
      Files.setPosixFilePermissions(
          path, PosixFilePermissions.fromString(directory ? "rwx------" : "rw-------"));
    } catch (UnsupportedOperationException ignored) {
      /* Windows uses the enclosing user profile ACL. */
    }
  }

  public void backup(Path target) throws SQLException, IOException {
    Path output = target.toAbsolutePath().normalize();
    if (Files.exists(output))
      throw new IOException("Choose a new backup filename; existing files are never overwritten.");
    Files.createDirectories(output.getParent());
    Path temporary = Files.createTempFile(output.getParent(), ".arns-backup-", ".zip");
    restrict(temporary, false);
    try {
      try (Connection c = connect();
          PreparedStatement s = c.prepareStatement("BACKUP TO ?")) {
        s.setString(1, temporary.toString());
        s.execute();
      }
      // Hard-link publication is exclusive: do not replace a concurrently created file.
      try {
        Files.createLink(output, temporary);
      } catch (UnsupportedOperationException e) {
        Files.copy(temporary, output);
      }
      restrict(output, false);
    } finally {
      Files.deleteIfExists(temporary);
    }
  }

  @Override
  public void close() throws SQLException {
    keeper.close();
  }
}
