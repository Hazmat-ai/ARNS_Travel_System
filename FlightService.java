package service;

import model.Flight;

import java.util.ArrayList;
import java.util.List;


/**
 * FlightService
 *
 * Acts as a lightweight in-memory data provider for flights. This service
 * stores sample flight data, provides read access, and offers a simple
 * search utility used by the UI. In a real system this would connect to
 * a database, but for this project it behaves as a static dataset.
 */
public class FlightService {
    // Internal storage of flights (acts like a mock database)
    private static final List<Flight> flights = new ArrayList<>();

    static {
        // Sample flights (origin -> destination)
        flights.add(new Flight("AC101", "Toronto", "Paris", "2025-12-01", 750.00));
        flights.add(new Flight("DL220", "Toronto", "New York", "2025-12-02", 220.00));
        flights.add(new Flight("BA305", "Toronto", "London", "2025-12-03", 700.00));
        flights.add(new Flight("AF410", "Toronto", "Paris", "2025-12-04", 820.00));
        flights.add(new Flight("LH500", "Toronto", "Frankfurt", "2025-12-05", 780.00));
    }

    /**
     * Return a copy of all flights.
     */
    public static List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    /**
     * Searches flights based on origin, destination, and/or date.
     * Any parameter that is blank or null is ignored in the filter.
     *
     * @param origin       optional origin filter
     * @param destination  optional destination filter
     * @param date         optional date filter
     * @return list of matching flights based on provided criteria
     */
    public static List<Flight> search(String origin, String destination, String date) {
        List<Flight> out = new ArrayList<>();
        for (Flight f : flights) {
            boolean ok = true;
            if (origin != null && !origin.trim().isEmpty() && !f.getOrigin().equalsIgnoreCase(origin.trim())) ok = false;
            if (destination != null && !destination.trim().isEmpty() && !f.getDestination().equalsIgnoreCase(destination.trim())) ok = false;
            if (date != null && !date.trim().isEmpty() && !f.getDate().equalsIgnoreCase(date.trim())) ok = false;
            if (ok) out.add(f);
        }
        return out;
    }
    /**
     * Adds a new flight to the internal list.
     * Used mainly for testing or extending sample data.
     */
    public static void addSampleFlight(Flight f) { flights.add(f); }
    
    /**
     * Clears all stored flights.
     * Primarily used for debugging or resetting state.
     */
    public static void clearAll() { flights.clear(); }
}
