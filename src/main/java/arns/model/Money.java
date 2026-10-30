package arns.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Fixed CAD minor units; no binary floating point or parsing of presentation strings. */
public record Money(long cents) implements Comparable<Money> {
  public Money {
    if (cents < 0) throw new IllegalArgumentException("Amount is out of range.");
  }

  public static Money of(String decimal) {
    return new Money(
        new BigDecimal(decimal)
            .setScale(2, RoundingMode.UNNECESSARY)
            .movePointRight(2)
            .longValueExact());
  }

  public Money multiply(int units) {
    return new Money(Math.multiplyExact(cents, Checks.units(units)));
  }

  public BigDecimal decimal() {
    return BigDecimal.valueOf(cents, 2);
  }

  public String display() {
    return "CAD " + decimal().toPlainString();
  }

  @Override
  public int compareTo(Money other) {
    return Long.compare(cents, other.cents);
  }
}
