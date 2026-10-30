package arns.model;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public final class Checks {
  private Checks() {}

  public static String text(String value, String field, int max) {
    Objects.requireNonNull(value, field + " is required.");
    String result = value.strip();
    if (result.isEmpty()
        || result.length() > max
        || result.codePoints().anyMatch(Character::isISOControl))
      throw new IllegalArgumentException(
          field + " must contain 1–" + max + " printable characters.");
    return result;
  }

  public static String email(String value) {
    String result = text(value, "Email", 254).toLowerCase(Locale.ROOT);
    if (!result.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Enter a valid email address.");
    return result;
  }

  public static int units(int value) {
    if (value < 1 || value > 365)
      throw new IllegalArgumentException("Quantity must be between 1 and 365.");
    return value;
  }

  public static LocalDate date(LocalDate value, LocalDate today) {
    Objects.requireNonNull(value, "Travel date is required.");
    if (value.isBefore(today) || value.isAfter(today.plusYears(3)))
      throw new IllegalArgumentException("Choose a date from today to three years ahead.");
    return value;
  }
}
