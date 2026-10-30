package arns.service;

import arns.data.Database;
import arns.model.Reservation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class CsvExport {
  private CsvExport() {}

  public static String cell(String value) {
    // Spreadsheet formula injection is separate from CSV quoting.
    String safe = value.matches("^[=+@\\-\\t\\r].*") ? "'" + value : value;
    return "\"" + safe.replace("\"", "\"\"") + "\"";
  }

  public static void write(Path destination, List<Reservation> rows) throws IOException {
    if (Files.exists(destination)) throw new IOException("Choose a new export filename.");
    StringBuilder text =
        new StringBuilder(
            "id,traveller,email,category,item,city,travel_date,units,unit_price_cad,total_cad,status\r\n");
    for (var r : rows) {
      String[] values = {
        r.id().toString(),
        r.traveller(),
        r.email(),
        r.item().category().label(),
        r.item().title(),
        r.item().city(),
        r.date().toString(),
        Integer.toString(r.units()),
        r.unitPrice().decimal().toPlainString(),
        r.total().decimal().toPlainString(),
        r.status().name()
      };
      text.append(
              java.util.Arrays.stream(values)
                  .map(CsvExport::cell)
                  .collect(java.util.stream.Collectors.joining(",")))
          .append("\r\n");
    }
    Path target = destination.toAbsolutePath().normalize();
    Files.createDirectories(target.getParent());
    Path temporary = Files.createTempFile(target.getParent(), ".arns-export-", ".csv");
    try {
      Database.restrict(temporary, false);
      Files.writeString(temporary, text, StandardCharsets.UTF_8);
      try {
        Files.createLink(target, temporary);
      } catch (UnsupportedOperationException e) {
        Files.copy(temporary, target);
      }
    } finally {
      Files.deleteIfExists(temporary);
    }
  }
}
