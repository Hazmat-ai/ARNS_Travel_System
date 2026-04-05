package model;


/**
 * Represents a flight in the system.
 *
 * This class acts as an immutable data model used to store basic flight details,
 * including flight number, origin, destination, date, and price. It provides
 * simple getters and a formatted toString for display purposes in the UI.
 */
public class Flight {
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final String date;
    private final double price;
    // Core flight information (immutable after construction)
    public Flight(String flightNumber, String origin, String destination, String date, double price) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.price = price;
    }

    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public double getPrice() { return price; }
    /**
     * Produces a formatted string representation of the flight,
     * used primarily for displaying inside lists and selection menus.
     */
    @Override
    public String toString() {
        return flightNumber + " | " + origin + " → " + destination + " | " + date + " | $" + String.format("%.2f", price);
    }
}
