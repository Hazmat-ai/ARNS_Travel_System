package arns.model;

public enum Category {
  FLIGHTS("Flights", "per traveller"),
  HOTELS("Hotels", "per night"),
  CARS("Cars", "per day"),
  ATTRACTIONS("Attractions", "per person"),
  EVENTS("Events", "per person"),
  PASSES("Passes", "per pass"),
  RESTAURANTS("Restaurants", "per person"),
  TOURS("Tours", "per person");
  private final String label;
  private final String unit;

  Category(String label, String unit) {
    this.label = label;
    this.unit = unit;
  }

  public String label() {
    return label;
  }

  public String unit() {
    return unit;
  }

  @Override
  public String toString() {
    return label;
  }
}
