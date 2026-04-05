package service;

import model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReservationService manages reservations (in-memory).
 * You can persist via FileManager if desired.
 */
public class ReservationService {
    private static final List<Reservation> reservations = new ArrayList<>();

    public static Reservation addReservation(Reservation r) {
        reservations.add(r);
        return r;
    }

    public static List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    public static List<Reservation> getReservationsForEmail(String email) {
        return reservations.stream().filter(r -> r.getEmail().equalsIgnoreCase(email)).collect(Collectors.toList());
    }

    public static void clearAll() {
        reservations.clear();
    }
}
