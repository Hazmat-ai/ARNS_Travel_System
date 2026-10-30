package arns.service;

import arns.data.LegacyDemoCatalog;
import arns.model.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/** Original project examples, deliberately labeled as unverified planning data. */
public final class CatalogService {
  public static final List<String> CITIES = List.of("Frankfurt", "London", "Paris", "New York");
  private final List<CatalogItem> items;

  public CatalogService() {
    List<CatalogItem> data = new ArrayList<>();
    for (String city : CITIES) {
      add(
          data,
          Category.FLIGHTS,
          "Toronto → " + city,
          city,
          "Illustrative route from Toronto. Dates, availability, taxes and baggage are not supplied"
              + " by an airline.",
          720 + CITIES.indexOf(city) * 25);
      for (var h : LegacyDemoCatalog.getHotelsForCity(city))
        add(
            data,
            Category.HOTELS,
            h.name,
            city,
            h.description
                + ". "
                + h.stars
                + "-star label from the original demo catalog; unverified.",
            h.pricePerNight);
      for (var c : LegacyDemoCatalog.getCarsForCity(city))
        add(
            data,
            Category.CARS,
            c.company + " · " + c.model,
            city,
            c.type + " vehicle example. No rental availability or insurance quote.",
            c.pricePerDay);
      for (var a : LegacyDemoCatalog.getAttractionsForCity(city))
        add(
            data,
            Category.ATTRACTIONS,
            a.name,
            city,
            a.type + " activity from the original demonstration catalog.",
            a.price);
      for (var e : LegacyDemoCatalog.getEventsForCity(city))
        add(
            data,
            Category.EVENTS,
            e.name,
            city,
            "Undated planning example. The original 2025 event date is not presented as current"
                + " availability.",
            e.price);
      for (var p : LegacyDemoCatalog.getPassesForCity(city))
        add(
            data,
            Category.PASSES,
            p.name,
            city,
            p.durationDays
                + "-day pass example. Entitlements and availability require supplier verification.",
            p.price);
      for (var r : LegacyDemoCatalog.getRestaurantsForCity(city))
        add(
            data,
            Category.RESTAURANTS,
            r.name,
            city,
            r.cuisine
                + ". Illustrative meal budget per person, not a table reservation or fixed menu"
                + " price.",
            r.avgPrice);
      for (var t : LegacyDemoCatalog.getToursForCity(city))
        add(
            data,
            Category.TOURS,
            t.name,
            city,
            t.duration + " tour example. No supplier inventory is connected.",
            t.price);
    }
    items = List.copyOf(data);
  }

  private static void add(
      List<CatalogItem> out,
      Category category,
      String title,
      String city,
      String description,
      double amount) {
    String id =
        UUID.nameUUIDFromBytes(
                (category.name() + "|" + city + "|" + title).getBytes(StandardCharsets.UTF_8))
            .toString();
    out.add(
        new CatalogItem(
            id,
            category,
            title,
            city,
            description,
            Money.of(BigDecimal.valueOf(amount).toPlainString())));
  }

  public List<CatalogItem> all() {
    return items;
  }

  public CatalogItem find(String id) {
    return items.stream()
        .filter(i -> i.id().equals(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Catalog item not found."));
  }

  public List<CatalogItem> search(String city, Category category, String query, boolean priceSort) {
    String needle = Objects.requireNonNullElse(query, "").strip().toLowerCase(Locale.ROOT);
    Predicate<CatalogItem> match =
        i ->
            (city == null || i.city().equals(city))
                && (category == null || i.category() == category)
                && (i.title() + " " + i.description()).toLowerCase(Locale.ROOT).contains(needle);
    Comparator<CatalogItem> order =
        priceSort
            ? Comparator.comparing(CatalogItem::price).thenComparing(CatalogItem::title)
            : Comparator.comparing(CatalogItem::title);
    return items.stream().filter(match).sorted(order).toList();
  }
}
