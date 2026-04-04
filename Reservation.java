package model;

import java.util.UUID;

/**
 * Reservation holds a simple reservation record for any module item (flight, hotel, tour...).
 * For flexibility we store a human-readable title and module name alongside the Flight if applicable.
 */
public class Reservation {
    private final String id;
    private final String name;       // person who booked
    private final String email;
    private final String module;     // "Flights", "Hotels", etc.
    private final String itemTitle;  // human-readable title (e.g., flight.toString() or hotel name)
    private final double price;

    public Reservation(String name, String email, String module, String itemTitle, double price) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.module = module;
        this.itemTitle = itemTitle;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getModule() { return module; }
    public String getItemTitle() { return itemTitle; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "[" + module + "] " + itemTitle + " — " + name + " (" + email + ") $" + String.format("%.2f", price);
    }
}
