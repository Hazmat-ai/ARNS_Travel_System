package arns.model;

import java.util.Objects;

public record CatalogItem(
    String id, Category category, String title, String city, String description, Money price) {
  public CatalogItem {
    id = Checks.text(id, "Item ID", 80);
    title = Checks.text(title, "Item", 200);
    city = Checks.text(city, "City", 100);
    description = Checks.text(description, "Description", 2000);
    Objects.requireNonNull(category);
    Objects.requireNonNull(price);
    if (price.cents() > 100_000_000L)
      throw new IllegalArgumentException("Unit price is out of range.");
  }
}
