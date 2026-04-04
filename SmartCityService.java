package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SmartCityService: realistic sample data for Hotels, Restaurants, Cars, Attractions, Events, Passes, Tours.
 * Includes 20 items per category for Frankfurt, London, Paris, and New York.
 *
 * Filtering API: getXForCity(String city) - uses equalsIgnoreCase to match the city field.
 */
public class SmartCityService {

    // -------- inner data classes --------
    public static class Hotel {
        public final String name;
        public final String city;
        public final int stars;
        public final double pricePerNight;
        public final String description;
        public Hotel(String name, String city, int stars, double pricePerNight, String description) {
            this.name = name; this.city = city; this.stars = stars; this.pricePerNight = pricePerNight; this.description = description;
        }
    }

    public static class Restaurant {
        public final String name;
        public final String city;
        public final String cuisine;
        public final double avgPrice;
        public Restaurant(String name, String city, String cuisine, double avgPrice) {
            this.name = name; this.city = city; this.cuisine = cuisine; this.avgPrice = avgPrice;
        }
    }

    public static class Car {
        public final String company;
        public final String model;
        public final String city;
        public final String type;
        public final double pricePerDay;
        public Car(String company, String model, String city, String type, double pricePerDay) {
            this.company = company; this.model = model; this.city = city; this.type = type; this.pricePerDay = pricePerDay;
        }
    }

    public static class Attraction {
        public final String name;
        public final String city;
        public final String type;
        public final double price;
        public Attraction(String name, String city, String type, double price) {
            this.name = name; this.city = city; this.type = type; this.price = price;
        }
    }

    public static class Event {
        public final String name;
        public final String city;
        public final String date;
        public final double price;
        public Event(String name, String city, String date, double price) {
            this.name = name; this.city = city; this.date = date; this.price = price;
        }
    }

    public static class Pass {
        public final String name;
        public final String city;
        public final int durationDays;
        public final double price;
        public Pass(String name, String city, int durationDays, double price) {
            this.name = name; this.city = city; this.durationDays = durationDays; this.price = price;
        }
    }

    public static class Tour {
        public final String name;
        public final String city;
        public final String duration;
        public final double price;
        public Tour(String name, String city, String duration, double price) {
            this.name = name; this.city = city; this.duration = duration; this.price = price;
        }
    }

    // ---------- Sample data ----------
    private static final List<Hotel> hotels = new ArrayList<>();
    private static final List<Restaurant> restaurants = new ArrayList<>();
    private static final List<Car> cars = new ArrayList<>();
    private static final List<Attraction> attractions = new ArrayList<>();
    private static final List<Event> events = new ArrayList<>();
    private static final List<Pass> passes = new ArrayList<>();
    private static final List<Tour> tours = new ArrayList<>();

    static {
        // ----------------- FRANKFURT (Germany) -----------------
        hotels.add(new Hotel("Frankfurt Marriott Hotel", "Frankfurt", 5, 210, "Modern rooms near the river Main"));
        hotels.add(new Hotel("Jumeirah Frankfurt", "Frankfurt", 5, 320, "Luxury hotel with city views"));
        hotels.add(new Hotel("Roomers Frankfurt", "Frankfurt", 4, 160, "Boutique hotel with spa"));
        hotels.add(new Hotel("Steigenberger Frankfurter Hof", "Frankfurt", 5, 280, "Historic grand hotel in the financial district"));
        hotels.add(new Hotel("Hotel Villa Orange", "Frankfurt", 4, 120, "Charming converted villa with private feel"));
        hotels.add(new Hotel("Leonardo Royal Hotel", "Frankfurt", 4, 140, "Comfortable business hotel"));
        hotels.add(new Hotel("Moxy Frankfurt East", "Frankfurt", 3, 95, "Trendy budget option close to attractions"));
        hotels.add(new Hotel("NH Collection Frankfurt City", "Frankfurt", 4, 150, "Stylish hotel near Hauptbahnhof"));
        hotels.add(new Hotel("Hilton Frankfurt City Centre", "Frankfurt", 5, 220, "Riverside hotel with gym"));
        hotels.add(new Hotel("Adina Apartment Hotel", "Frankfurt", 4, 170, "Apartment-style rooms ideal for families"));
        hotels.add(new Hotel("25hours Hotel The Trip", "Frankfurt", 4, 130, "Playful design hotel with rooftop bar"));
        hotels.add(new Hotel("Scandic Frankfurt Museumsufer", "Frankfurt", 4, 135, "Close to museums and old town"));
        hotels.add(new Hotel("Hotel Hamburger Hof", "Frankfurt", 4, 115, "Comfortable and centrally located"));
        hotels.add(new Hotel("The Pure", "Frankfurt", 4, 125, "Contemporary design near banking district"));
        hotels.add(new Hotel("NH Frankfurt Messe", "Frankfurt", 4, 110, "Convenient for exhibition center visitors"));
        hotels.add(new Hotel("Best Western Premier", "Frankfurt", 4, 105, "Reliable mid-range hotel"));
        hotels.add(new Hotel("INNSiDE by Meliá Frankfurt", "Frankfurt", 4, 145, "Modern rooms and amenities"));
        hotels.add(new Hotel("Sofitel Frankfurt Opera", "Frankfurt", 5, 300, "Luxury by the opera house"));
        hotels.add(new Hotel("Hotel Josefshof am Rathaus", "Frankfurt", 3, 85, "Small hotel in the old town"));
        hotels.add(new Hotel("Hotel Monopol Frankfurt", "Frankfurt", 4, 110, "Classic hotel with friendly service"));

        restaurants.add(new Restaurant("Restaurant Gustav", "Frankfurt", "Contemporary German", 45));
        restaurants.add(new Restaurant("Apfelwein Wagner", "Frankfurt", "Traditional German", 18));
        restaurants.add(new Restaurant("Gastrobar Margarete", "Frankfurt", "European", 40));
        restaurants.add(new Restaurant("Restaurant Francais", "Frankfurt", "French", 65));
        restaurants.add(new Restaurant("Mian", "Frankfurt", "Asian Fusion", 30));
        restaurants.add(new Restaurant("Zenzakan Frankfurt", "Frankfurt", "Pan-Asian", 55));
        restaurants.add(new Restaurant("Kaffeemühlen Bistro", "Frankfurt", "Cafe", 15));
        restaurants.add(new Restaurant("Lai Lai", "Frankfurt", "Chinese", 25));
        restaurants.add(new Restaurant("MainNizza", "Frankfurt", "Seafood", 48));
        restaurants.add(new Restaurant("La Cicala", "Frankfurt", "Italian", 35));
        restaurants.add(new Restaurant("Seven Swans Restaurant", "Frankfurt", "Vegetarian", 50));
        restaurants.add(new Restaurant("Villa Merton", "Frankfurt", "Modern European", 70));
        restaurants.add(new Restaurant("Im Herzfeld", "Frankfurt", "German", 22));
        restaurants.add(new Restaurant("Wiesenmühle", "Frankfurt", "German", 20));
        restaurants.add(new Restaurant("Fletchers Better Burgers", "Frankfurt", "Burgers", 18));
        restaurants.add(new Restaurant("Sora", "Frankfurt", "Japanese", 38));
        restaurants.add(new Restaurant("Riverside Grill", "Frankfurt", "Steakhouse", 55));
        restaurants.add(new Restaurant("Soul Kitchen", "Frankfurt", "Mediterranean", 28));
        restaurants.add(new Restaurant("Bacharach Weinlokal", "Frankfurt", "Wine Bar", 30));
        restaurants.add(new Restaurant("Kameha Grand Main Tower Restaurant", "Frankfurt", "Fine Dining", 95));

        cars.add(new Car("Sixt", "VW Golf", "Frankfurt", "Compact", 48));
        cars.add(new Car("Europcar", "Skoda Octavia", "Frankfurt", "Sedan", 55));
        cars.add(new Car("Hertz", "Ford Focus", "Frankfurt", "Compact", 50));
        cars.add(new Car("Avis", "Opel Astra", "Frankfurt", "Compact", 47));
        cars.add(new Car("Enterprise", "BMW 3 Series", "Frankfurt", "Premium", 85));
        cars.add(new Car("Sixt", "Mercedes C-Class", "Frankfurt", "Premium", 95));
        cars.add(new Car("Europcar", "VW Passat", "Frankfurt", "Estate", 60));
        cars.add(new Car("Avis", "Renault Captur", "Frankfurt", "SUV", 65));
        cars.add(new Car("Hertz", "Dacia Sandero", "Frankfurt", "Budget", 38));
        cars.add(new Car("Budget", "Toyota Corolla", "Frankfurt", "Compact", 45));
        cars.add(new Car("Sixt", "Audi A4", "Frankfurt", "Premium", 88));
        cars.add(new Car("Enterprise", "VW T-Roc", "Frankfurt", "SUV", 70));
        cars.add(new Car("Europcar", "Skoda Fabia", "Frankfurt", "Compact", 42));
        cars.add(new Car("Avis", "Ford Kuga", "Frankfurt", "SUV", 72));
        cars.add(new Car("Hertz", "Peugeot 208", "Frankfurt", "Compact", 44));
        cars.add(new Car("Sixt", "Opel Vivaro", "Frankfurt", "Van", 95));
        cars.add(new Car("Europcar", "Mercedes Vito", "Frankfurt", "Van", 110));
        cars.add(new Car("Avis", "Toyota Yaris", "Frankfurt", "Compact", 40));
        cars.add(new Car("Budget", "Seat Ibiza", "Frankfurt", "Compact", 41));
        cars.add(new Car("Enterprise", "Nissan Qashqai", "Frankfurt", "SUV", 68));

        attractions.add(new Attraction("Römer", "Frankfurt", "Historic", 0));
        attractions.add(new Attraction("Palmengarten", "Frankfurt", "Garden", 7));
        attractions.add(new Attraction("Städel Museum", "Frankfurt", "Museum", 16));
        attractions.add(new Attraction("Frankfurt Cathedral", "Frankfurt", "Historic", 0));
        attractions.add(new Attraction("Main Tower Observation Deck", "Frankfurt", "Viewpoint", 10));
        attractions.add(new Attraction("Museumsufer", "Frankfurt", "Museum District", 0));
        attractions.add(new Attraction("Zeil Shopping Street", "Frankfurt", "Shopping", 0));
        attractions.add(new Attraction("Senckenberg Natural History", "Frankfurt", "Museum", 14));
        attractions.add(new Attraction("Old Opera House", "Frankfurt", "Cultural", 0));
        attractions.add(new Attraction("Eiserner Steg Bridge", "Frankfurt", "Landmark", 0));
        attractions.add(new Attraction("Frankfurt Zoo", "Frankfurt", "Zoo", 12));
        attractions.add(new Attraction("Goethe House", "Frankfurt", "Historic", 8));
        attractions.add(new Attraction("Kleinmarkthalle", "Frankfurt", "Market", 0));
        attractions.add(new Attraction("Bockenheim University Quarter", "Frankfurt", "Neighborhood", 0));
        attractions.add(new Attraction("Grüneburgpark", "Frankfurt", "Park", 0));
        attractions.add(new Attraction("DialogMuseum", "Frankfurt", "Interactive", 13));
        attractions.add(new Attraction("Frankfurt Christmas Market", "Frankfurt", "Seasonal", 0));
        attractions.add(new Attraction("Batschkapp Music Venue", "Frankfurt", "Music", 25));
        attractions.add(new Attraction("Oper Frankfurt Performance", "Frankfurt", "Theatre", 35));
        attractions.add(new Attraction("Banking District Walk", "Frankfurt", "Walking Tour", 10));

        events.add(new Event("Frankfurt Book Fair (Sample Edition)", "Frankfurt", "2025-10-15", 25));
        events.add(new Event("Christmas Market Opening", "Frankfurt", "2025-11-22", 0));
        events.add(new Event("Food & Wine Festival", "Frankfurt", "2025-06-05", 30));
        events.add(new Event("Indie Music Night", "Frankfurt", "2025-07-18", 20));
        events.add(new Event("Contemporary Art Fair", "Frankfurt", "2025-09-10", 18));
        events.add(new Event("Open Air Cinema", "Frankfurt", "2025-08-12", 12));
        events.add(new Event("Classical Concert Series", "Frankfurt", "2025-05-20", 40));
        events.add(new Event("Riverboat Jazz Cruise", "Frankfurt", "2025-06-28", 35));
        events.add(new Event("Riverside Running Race", "Frankfurt", "2025-04-30", 15));
        events.add(new Event("Tech Meet-up Conference", "Frankfurt", "2025-03-22", 80));
        events.add(new Event("Local Farmers Market", "Frankfurt", "2025-05-02", 0));
        events.add(new Event("Street Food Weekend", "Frankfurt", "2025-07-04", 10));
        events.add(new Event("Outdoor Yoga Session", "Frankfurt", "2025-06-02", 8));
        events.add(new Event("Photography Walk", "Frankfurt", "2025-05-14", 12));
        events.add(new Event("Opera Gala Night", "Frankfurt", "2025-09-22", 75));
        events.add(new Event("Bike Tour Meetup", "Frankfurt", "2025-04-11", 20));
        events.add(new Event("Artisan Crafts Fair", "Frankfurt", "2025-10-05", 5));
        events.add(new Event("Late Night Museum", "Frankfurt", "2025-08-01", 7));
        events.add(new Event("Frankfurt Marathon (example)", "Frankfurt", "2025-09-07", 45));
        events.add(new Event("Rooftop Food Festival", "Frankfurt", "2025-07-30", 22));

        passes.add(new Pass("Frankfurt Museum Pass", "Frankfurt", 3, 45));
        passes.add(new Pass("City Highlights Pass", "Frankfurt", 2, 35));
        passes.add(new Pass("Museumsufer Multi-Pass", "Frankfurt", 5, 60));
        passes.add(new Pass("River Cruise & Museum Combo", "Frankfurt", 1, 30));
        passes.add(new Pass("Family Attractions Pass", "Frankfurt", 3, 80));
        passes.add(new Pass("Frankfurt Nightlife Pass", "Frankfurt", 1, 28));
        passes.add(new Pass("Art Lover's Pass", "Frankfurt", 4, 55));
        passes.add(new Pass("Student Saver Pass", "Frankfurt", 2, 25));
        passes.add(new Pass("Cultural Circuit Pass", "Frankfurt", 3, 48));
        passes.add(new Pass("Seasonal Market Pass", "Frankfurt", 1, 10));
        passes.add(new Pass("City Explorer Lite", "Frankfurt", 2, 22));
        passes.add(new Pass("Historic Sites Pass", "Frankfurt", 3, 40));
        passes.add(new Pass("Frankfurt Adventure Pass", "Frankfurt", 5, 95));
        passes.add(new Pass("Museum & Zoo Pass", "Frankfurt", 3, 50));
        passes.add(new Pass("Foodie Tour Pass", "Frankfurt", 1, 30));
        passes.add(new Pass("Walking Tour Bundle", "Frankfurt", 2, 24));
        passes.add(new Pass("Family Fun Pass", "Frankfurt", 4, 70));
        passes.add(new Pass("Premium Cultural Pass", "Frankfurt", 7, 120));
        passes.add(new Pass("Green City Pass", "Frankfurt", 2, 34));
        passes.add(new Pass("Local Transit + Attractions", "Frankfurt", 3, 65));

        tours.add(new Tour("Old Town Walking Tour", "Frankfurt", "2h", 18));
        tours.add(new Tour("Riverside Boat Tour", "Frankfurt", "1.5h", 22));
        tours.add(new Tour("Culinary Tasting Tour", "Frankfurt", "3h", 45));
        tours.add(new Tour("Historical Highlights Tour", "Frankfurt", "2.5h", 25));
        tours.add(new Tour("Architecture Photography Tour", "Frankfurt", "3h", 30));
        tours.add(new Tour("Bike & Park Tour", "Frankfurt", "3h", 28));
        tours.add(new Tour("Museum Guided Tour", "Frankfurt", "2h", 20));
        tours.add(new Tour("Evening Ghost Walk", "Frankfurt", "1.5h", 15));
        tours.add(new Tour("Food Market Exploration", "Frankfurt", "2h", 20));
        tours.add(new Tour("Family Adventure Tour", "Frankfurt", "4h", 55));
        tours.add(new Tour("Wine Tasting Excursion", "Frankfurt", "3h", 60));
        tours.add(new Tour("Local Markets & Crafts Tour", "Frankfurt", "2h", 18));
        tours.add(new Tour("Photography Sunrise Walk", "Frankfurt", "1.5h", 16));
        tours.add(new Tour("Historic Churches Tour", "Frankfurt", "2h", 17));
        tours.add(new Tour("Premium Private Tour", "Frankfurt", "3h", 120));
        tours.add(new Tour("Boat & Dine Cruise", "Frankfurt", "2h", 70));
        tours.add(new Tour("Runner's City Route", "Frankfurt", "1h", 10));
        tours.add(new Tour("Architectural Skyline Tour", "Frankfurt", "2h", 24));
        tours.add(new Tour("Hidden Alleys Walk", "Frankfurt", "1.5h", 14));
        tours.add(new Tour("Mainhattan Photo Tour", "Frankfurt", "2h", 26));

        // ----------------- LONDON (UK) -----------------
        hotels.add(new Hotel("The Savoy", "London", 5, 620, "Iconic luxury hotel on the Strand"));
        hotels.add(new Hotel("The Ritz London", "London", 5, 750, "Classic luxury, near Green Park"));
        hotels.add(new Hotel("Claridge's", "London", 5, 690, "Elegant Art Deco hotel in Mayfair"));
        hotels.add(new Hotel("The Langham", "London", 5, 420, "Historic luxury near Regent Street"));
        hotels.add(new Hotel("Corinthia London", "London", 5, 540, "High-end spa and dining"));
        hotels.add(new Hotel("The Hoxton, Holborn", "London", 4, 180, "Trendy rooms in central London"));
        hotels.add(new Hotel("Meliá White House", "London", 4, 160, "Comfortable and stylish rooms"));
        hotels.add(new Hotel("Premier Inn London City", "London", 3, 95, "Reliable budget option"));
        hotels.add(new Hotel("DoubleTree by Hilton", "London", 4, 170, "Comfort and convenience"));
        hotels.add(new Hotel("Shangri-La The Shard", "London", 5, 680, "Dramatic views from the Shard"));
        hotels.add(new Hotel("The Ned", "London", 5, 410, "Converted bank with lively atmosphere"));
        hotels.add(new Hotel("CitizenM Tower of London", "London", 4, 140, "Modern compact rooms"));
        hotels.add(new Hotel("The Clermont", "London", 4, 170, "Near Marble Arch"));
        hotels.add(new Hotel("Zetter Townhouse", "London", 4, 195, "Quirky boutique in Clerkenwell"));
        hotels.add(new Hotel("The Kings Arms", "London", 3, 85, "Cozy pub-hotel in Bloomsbury"));
        hotels.add(new Hotel("The Beaumont", "London", 5, 520, "Luxury near Mayfair"));
        hotels.add(new Hotel("The Curtain", "London", 5, 260, "Stylish Shoreditch hotel"));
        hotels.add(new Hotel("St. Pancras Renaissance Hotel", "London", 5, 340, "Victorian grandeur by the station"));
        hotels.add(new Hotel("The Soho Hotel", "London", 5, 430, "Boutique in the theatre district"));
        hotels.add(new Hotel("The Nadler Kensington", "London", 4, 150, "Apartment-style comfort"));

        restaurants.add(new Restaurant("Dishoom Shoreditch", "London", "Indian", 28));
        restaurants.add(new Restaurant("Gordon Ramsay", "London", "Modern European", 150));
        restaurants.add(new Restaurant("The Ledbury", "London", "Fine Dining", 180));
        restaurants.add(new Restaurant("Sketch (The Gallery)", "London", "Modern European", 110));
        restaurants.add(new Restaurant("Flat Iron", "London", "Steakhouse", 40));
        restaurants.add(new Restaurant("Bao Fitzrovia", "London", "Taiwanese", 30));
        restaurants.add(new Restaurant("Hakkasan Mayfair", "London", "Chinese", 95));
        restaurants.add(new Restaurant("Palomar", "London", "Mediterranean", 60));
        restaurants.add(new Restaurant("Padella", "London", "Italian", 25));
        restaurants.add(new Restaurant("Barrafina", "London", "Spanish Tapas", 45));
        restaurants.add(new Restaurant("Rules Restaurant", "London", "British", 70));
        restaurants.add(new Restaurant("Berners Tavern", "London", "Modern British", 85));
        restaurants.add(new Restaurant("Duck & Waffle", "London", "British", 38));
        restaurants.add(new Restaurant("Borough Market Stalls", "London", "Street Food", 15));
        restaurants.add(new Restaurant("The Palomar", "London", "Levantine", 55));
        restaurants.add(new Restaurant("Sketch Lecture Room", "London", "Gastronomic", 220));
        restaurants.add(new Restaurant("Humble Pizza", "London", "Pizza", 18));
        restaurants.add(new Restaurant("Franco Manca", "London", "Pizza", 12));
        restaurants.add(new Restaurant("Flat Iron Covent Garden", "London", "Steakhouse", 42));
        restaurants.add(new Restaurant("The Wolseley", "London", "European", 60));

        cars.add(new Car("Enterprise", "Ford Fiesta", "London", "Compact", 55));
        cars.add(new Car("Hertz", "Vauxhall Astra", "London", "Compact", 58));
        cars.add(new Car("Europcar", "VW Passat", "London", "Estate", 75));
        cars.add(new Car("Avis", "BMW 3 Series", "London", "Premium", 95));
        cars.add(new Car("Sixt", "Mercedes C-Class", "London", "Luxury", 120));
        cars.add(new Car("Green Motion", "Toyota Prius", "London", "Hybrid", 65));
        cars.add(new Car("Enterprise", "Nissan Qashqai", "London", "SUV", 85));
        cars.add(new Car("Budget", "Renault Clio", "London", "Compact", 50));
        cars.add(new Car("Alamo", "Kia Sportage", "London", "SUV", 80));
        cars.add(new Car("Avis", "Ford Focus", "London", "Compact", 52));
        cars.add(new Car("Sixt", "Audi A4", "London", "Premium", 110));
        cars.add(new Car("Europcar", "Vauxhall Corsa", "London", "Compact", 48));
        cars.add(new Car("Hertz", "Toyota Corolla", "London", "Compact", 54));
        cars.add(new Car("Enterprise", "Ford Kuga", "London", "SUV", 82));
        cars.add(new Car("Green Motion", "Nissan Leaf", "London", "Electric", 70));
        cars.add(new Car("Sixt", "Range Rover Evoque", "London", "Luxury SUV", 160));
        cars.add(new Car("Avis", "Citroen C3", "London", "Compact", 47));
        cars.add(new Car("Budget", "Fiat 500", "London", "Mini", 45));
        cars.add(new Car("Alamo", "Skoda Octavia", "London", "Estate", 72));
        cars.add(new Car("Europcar", "Mercedes V-Class", "London", "Van", 140));

        attractions.add(new Attraction("Tower of London", "London", "Historic", 35));
        attractions.add(new Attraction("British Museum", "London", "Museum", 0));
        attractions.add(new Attraction("London Eye", "London", "Observation", 40));
        attractions.add(new Attraction("Natural History Museum", "London", "Museum", 0));
        attractions.add(new Attraction("Buckingham Palace", "London", "Royal", 30));
        attractions.add(new Attraction("Tate Modern", "London", "Museum", 0));
        attractions.add(new Attraction("Camden Market", "London", "Market", 0));
        attractions.add(new Attraction("Kew Gardens", "London", "Garden", 20));
        attractions.add(new Attraction("St. Paul's Cathedral", "London", "Religious", 18));
        attractions.add(new Attraction("Westminster Abbey", "London", "Religious", 24));
        attractions.add(new Attraction("Covent Garden", "London", "Shopping", 0));
        attractions.add(new Attraction("Shakespeare's Globe", "London", "Theatre", 15));
        attractions.add(new Attraction("Hyde Park", "London", "Park", 0));
        attractions.add(new Attraction("Soho Food Walk", "London", "Food Tour", 28));
        attractions.add(new Attraction("The Shard Observation", "London", "Viewpoint", 32));
        attractions.add(new Attraction("Piccadilly Circus", "London", "Landmark", 0));
        attractions.add(new Attraction("Kensington Palace", "London", "Historic", 20));
        attractions.add(new Attraction("Museum of London", "London", "Museum", 0));
        attractions.add(new Attraction("Leadenhall Market", "London", "Market", 0));
        attractions.add(new Attraction("Tower Bridge Exhibition", "London", "Historic", 12));

        events.add(new Event("Wimbledon Championships (sample)", "London", "2025-07-01", 250));
        events.add(new Event("Royal Opera House Gala", "London", "2025-06-15", 120));
        events.add(new Event("London Fashion Week (sample)", "London", "2025-09-12", 180));
        events.add(new Event("Notting Hill Carnival", "London", "2025-08-24", 0));
        events.add(new Event("London Marathon (sample)", "London", "2025-04-21", 60));
        events.add(new Event("Chelsea Flower Show", "London", "2025-05-20", 35));
        events.add(new Event("Trooping the Colour", "London", "2025-06-13", 0));
        events.add(new Event("Open Air Jazz Night", "London", "2025-07-19", 30));
        events.add(new Event("West End Theatre Week", "London", "2025-11-10", 45));
        events.add(new Event("Food Truck Festival", "London", "2025-06-08", 10));
        events.add(new Event("Tech Start-up Conference", "London", "2025-10-03", 120));
        events.add(new Event("Art & Design Fair", "London", "2025-09-05", 25));
        events.add(new Event("Outdoor Cinema Summer", "London", "2025-08-03", 12));
        events.add(new Event("Historic Reenactment Day", "London", "2025-05-11", 8));
        events.add(new Event("Classical Concert Series", "London", "2025-06-02", 50));
        events.add(new Event("Street Arts Festival", "London", "2025-07-26", 15));
        events.add(new Event("Vintage Market Weekend", "London", "2025-09-14", 5));
        events.add(new Event("River Thames Boat Party", "London", "2025-07-10", 40));
        events.add(new Event("Christmas Lights Switch-On", "London", "2025-11-29", 0));
        events.add(new Event("London Beer Festival", "London", "2025-09-21", 22));

        passes.add(new Pass("London Pass", "London", 2, 120));
        passes.add(new Pass("City Explorer Pass", "London", 3, 160));
        passes.add(new Pass("Museum & Transport Combo", "London", 2, 70));
        passes.add(new Pass("West End Show Pass", "London", 1, 85));
        passes.add(new Pass("Royal Attractions Pass", "London", 3, 125));
        passes.add(new Pass("Family Fun Pack", "London", 3, 140));
        passes.add(new Pass("Historic Sites Pass", "London", 2, 65));
        passes.add(new Pass("Kew Gardens + Attractions", "London", 1, 40));
        passes.add(new Pass("London Nightlife Pass", "London", 1, 55));
        passes.add(new Pass("Theatre Sampler Pass", "London", 2, 95));
        passes.add(new Pass("London Markets Pass", "London", 1, 25));
        passes.add(new Pass("Art Lovers Pass", "London", 3, 110));
        passes.add(new Pass("Garden & Museums Pass", "London", 4, 140));
        passes.add(new Pass("Transport & Attractions", "London", 3, 100));
        passes.add(new Pass("Student Savings Pass", "London", 2, 45));
        passes.add(new Pass("Premium Access Pass", "London", 5, 220));
        passes.add(new Pass("Local Experiences Bundle", "London", 2, 80));
        passes.add(new Pass("Family Theatre Package", "London", 2, 130));
        passes.add(new Pass("Food & Drink Pass", "London", 1, 48));
        passes.add(new Pass("City Sightseeing Combo", "London", 3, 95));

        tours.add(new Tour("Thames River Cruise", "London", "1.5h", 30));
        tours.add(new Tour("Historic London Walking Tour", "London", "2.5h", 25));
        tours.add(new Tour("Westminster & Parliament Tour", "London", "2h", 20));
        tours.add(new Tour("East End Street Art Tour", "London", "2h", 22));
        tours.add(new Tour("Harry Potter Studio Half-Day", "London", "4h", 75));
        tours.add(new Tour("Pub History Crawl", "London", "3h", 35));
        tours.add(new Tour("Hidden Gems of London", "London", "2h", 28));
        tours.add(new Tour("Royal Parks Bike Tour", "London", "3h", 32));
        tours.add(new Tour("London Photography Tour", "London", "2h", 26));
        tours.add(new Tour("Culinary East London Tour", "London", "3h", 45));
        tours.add(new Tour("Guide to the West End", "London", "2h", 24));
        tours.add(new Tour("Camden Market & Canal Tour", "London", "2.5h", 20));
        tours.add(new Tour("Shoreditch Trend Tour", "London", "2h", 22));
        tours.add(new Tour("Private Chauffeur City Tour", "London", "3h", 180));
        tours.add(new Tour("Royal Palaces Guided Tour", "London", "3h", 40));
        tours.add(new Tour("London By Night Tour", "London", "2h", 27));
        tours.add(new Tour("Crown Jewels & Tower Tour", "London", "2h", 34));
        tours.add(new Tour("Kew Gardens Guided Walk", "London", "1.5h", 18));
        tours.add(new Tour("Vintage Bus City Tour", "London", "1.5h", 22));
        tours.add(new Tour("Hidden Alleyways Food Tour", "London", "2.5h", 48));

        // ----------------- PARIS (France) -----------------
        hotels.add(new Hotel("Le Bristol Paris", "Paris", 5, 920, "Classic luxury near Champs-Élysées"));
        hotels.add(new Hotel("Hôtel de Crillon", "Paris", 5, 880, "Luxury palace hotel on Place de la Concorde"));
        hotels.add(new Hotel("Shangri-La Hotel, Paris", "Paris", 5, 760, "Elegant rooms with Eiffel Tower views"));
        hotels.add(new Hotel("Le Meurice", "Paris", 5, 840, "Iconic palace hotel with Michelin dining"));
        hotels.add(new Hotel("Hôtel Plaza Athénée", "Paris", 5, 970, "High-end fashion district luxury"));
        hotels.add(new Hotel("Hotel Le Six", "Paris", 4, 210, "Boutique hotel in Saint-Germain"));
        hotels.add(new Hotel("Hotel La Comtesse", "Paris", 4, 195, "Stylish rooms near the Eiffel Tower"));
        hotels.add(new Hotel("CitizenM Paris Gare de Lyon", "Paris", 4, 135, "Modern compact rooms"));
        hotels.add(new Hotel("Novotel Paris Centre Tour Eiffel", "Paris", 4, 185, "Convenient near the Eiffel Tower"));
        hotels.add(new Hotel("Hotel Regina Louvre", "Paris", 5, 300, "Historic charm across from the Louvre"));
        hotels.add(new Hotel("Le Roch Hotel & Spa", "Paris", 5, 420, "Boutique luxury with spa"));
        hotels.add(new Hotel("Hotel Ekta Champs-Élysées", "Paris", 4, 220, "Fashion-forward boutique hotel"));
        hotels.add(new Hotel("Hotel du Louvre", "Paris", 5, 360, "Elegant hotel beside the Louvre"));
        hotels.add(new Hotel("Hotel des Grands Boulevards", "Paris", 4, 200, "Trendy neighborhood stay"));
        hotels.add(new Hotel("Hôtel Le Relais Saint-Germain", "Paris", 4, 210, "Classic Parisian feel"));
        hotels.add(new Hotel("Hotel Thérèse", "Paris", 4, 165, "Quiet boutique near Opera"));
        hotels.add(new Hotel("Hotel Westminster", "Paris", 5, 410, "Belle Époque luxury"));
        hotels.add(new Hotel("Mercure Paris Centre", "Paris", 4, 140, "Reliable mid-range near attractions"));
        hotels.add(new Hotel("Hotel Eiffel Turenne", "Paris", 3, 120, "Affordable near the tower"));
        hotels.add(new Hotel("Hotel Pont Royal", "Paris", 4, 230, "Charming riverside boutique"));

        restaurants.add(new Restaurant("Le Jules Verne", "Paris", "French", 160));
        restaurants.add(new Restaurant("L'Ambroisie", "Paris", "French", 220));
        restaurants.add(new Restaurant("Septime", "Paris", "Modern French", 140));
        restaurants.add(new Restaurant("Chez L'Ami Jean", "Paris", "Basque", 60));
        restaurants.add(new Restaurant("Le Comptoir du Relais", "Paris", "Bistro", 45));
        restaurants.add(new Restaurant("Frenchie", "Paris", "Modern European", 95));
        restaurants.add(new Restaurant("L'As du Fallafel", "Paris", "Middle Eastern", 12));
        restaurants.add(new Restaurant("Le Cinq", "Paris", "Fine Dining", 300));
        restaurants.add(new Restaurant("La Tour d'Argent", "Paris", "Historic French", 250));
        restaurants.add(new Restaurant("Cafe de Flore", "Paris", "Cafe", 30));
        restaurants.add(new Restaurant("Bistrot Paul Bert", "Paris", "Bistro", 55));
        restaurants.add(new Restaurant("Café Constant", "Paris", "French", 40));
        restaurants.add(new Restaurant("Le Chateaubriand", "Paris", "Contemporary", 85));
        restaurants.add(new Restaurant("Epicure", "Paris", "Gastronomic", 350));
        restaurants.add(new Restaurant("La Petite Périgourdine", "Paris", "French", 48));
        restaurants.add(new Restaurant("Le Comptoir", "Paris", "Bistro", 42));
        restaurants.add(new Restaurant("Bouillon Chartier", "Paris", "Traditional", 20));
        restaurants.add(new Restaurant("L'Atelier de Joël Robuchon", "Paris", "Modern French", 170));
        restaurants.add(new Restaurant("Brasserie Balzar", "Paris", "French", 35));
        restaurants.add(new Restaurant("Pierre Gagnaire (sample)", "Paris", "Gastronomic", 300));

        cars.add(new Car("Hertz", "Renault Clio", "Paris", "Compact", 55));
        cars.add(new Car("Europcar", "Peugeot 308", "Paris", "Compact", 58));
        cars.add(new Car("Avis", "Citroën C4", "Paris", "Compact", 60));
        cars.add(new Car("Sixt", "Mercedes A-Class", "Paris", "Premium", 95));
        cars.add(new Car("Enterprise", "Toyota Yaris", "Paris", "Compact", 52));
        cars.add(new Car("Alamo", "VW Passat", "Paris", "Estate", 75));
        cars.add(new Car("Hertz", "DS 7 Crossback", "Paris", "SUV", 85));
        cars.add(new Car("Europcar", "Renault Captur", "Paris", "SUV", 70));
        cars.add(new Car("Avis", "Peugeot 2008", "Paris", "SUV", 72));
        cars.add(new Car("Sixt", "BMW 3 Series", "Paris", "Premium", 110));
        cars.add(new Car("Europcar", "Opel Corsa", "Paris", "Compact", 50));
        cars.add(new Car("Hertz", "Ford Focus", "Paris", "Compact", 56));
        cars.add(new Car("Avis", "Toyota Corolla", "Paris", "Compact", 62));
        cars.add(new Car("Sixt", "Audi A4", "Paris", "Premium", 115));
        cars.add(new Car("Enterprise", "Kia Niro", "Paris", "Hybrid", 68));
        cars.add(new Car("Alamo", "Skoda Superb", "Paris", "Estate", 78));
        cars.add(new Car("Europcar", "Mercedes Vito", "Paris", "Van", 120));
        cars.add(new Car("Budget", "Fiat 500", "Paris", "Mini", 44));
        cars.add(new Car("Hertz", "Nissan Qashqai", "Paris", "SUV", 80));
        cars.add(new Car("Sixt", "Renault Megane", "Paris", "Compact", 57));

        attractions.add(new Attraction("Eiffel Tower", "Paris", "Landmark", 25));
        attractions.add(new Attraction("Louvre Museum", "Paris", "Museum", 17));
        attractions.add(new Attraction("Notre-Dame Cathedral (visit area)", "Paris", "Historic", 0));
        attractions.add(new Attraction("Musée d'Orsay", "Paris", "Museum", 14));
        attractions.add(new Attraction("Montmartre & Sacré-Cœur", "Paris", "Historic", 0));
        attractions.add(new Attraction("Arc de Triomphe", "Paris", "Landmark", 12));
        attractions.add(new Attraction("Palace of Versailles (nearby)", "Paris", "Historic", 20));
        attractions.add(new Attraction("Centre Pompidou", "Paris", "Museum", 15));
        attractions.add(new Attraction("Sainte-Chapelle", "Paris", "Historic", 10));
        attractions.add(new Attraction("Luxembourg Gardens", "Paris", "Garden", 0));
        attractions.add(new Attraction("Rodin Museum", "Paris", "Museum", 13));
        attractions.add(new Attraction("Seine River Cruise", "Paris", "Cruise", 30));
        attractions.add(new Attraction("Père Lachaise Cemetery", "Paris", "Historic", 0));
        attractions.add(new Attraction("Pont Neuf Walk", "Paris", "Walking", 0));
        attractions.add(new Attraction("Moulin Rouge (cabaret area)", "Paris", "Entertainment", 85));
        attractions.add(new Attraction("Musée Rodin", "Paris", "Museum", 12));
        attractions.add(new Attraction("Palais Garnier (Opera)", "Paris", "Theatre", 30));
        attractions.add(new Attraction("Le Marais District", "Paris", "Neighborhood", 0));
        attractions.add(new Attraction("Latin Quarter Walking Tour", "Paris", "Walking", 18));
        attractions.add(new Attraction("Champs-Élysées Stroll", "Paris", "Shopping", 0));

        events.add(new Event("Paris Fashion Week (sample)", "Paris", "2025-09-25", 200));
        events.add(new Event("Bastille Day Celebrations", "Paris", "2025-07-14", 0));
        events.add(new Event("Nuit Blanche", "Paris", "2025-10-04", 0));
        events.add(new Event("Fête de la Musique", "Paris", "2025-06-21", 0));
        events.add(new Event("Paris Marathon (sample)", "Paris", "2025-04-13", 60));
        events.add(new Event("Wine & Cheese Festival", "Paris", "2025-05-16", 35));
        events.add(new Event("Outdoor Cinema Festival", "Paris", "2025-07-08", 12));
        events.add(new Event("Contemporary Dance Week", "Paris", "2025-11-02", 28));
        events.add(new Event("Artisan Market Weekend", "Paris", "2025-06-01", 5));
        events.add(new Event("Classical Concert at Sainte-Chapelle", "Paris", "2025-08-18", 45));
        events.add(new Event("Seine River Jazz Night", "Paris", "2025-07-20", 30));
        events.add(new Event("French Gastronomy Week", "Paris", "2025-10-11", 80));
        events.add(new Event("Light Festival", "Paris", "2025-12-01", 0));
        events.add(new Event("Photography Expo", "Paris", "2025-09-14", 20));
        events.add(new Event("Historic Tours Special", "Paris", "2025-05-23", 15));
        events.add(new Event("Paris Tech Meetup", "Paris", "2025-03-26", 40));
        events.add(new Event("Rooftop Food Fair", "Paris", "2025-06-30", 22));
        events.add(new Event("Opera Night Gala", "Paris", "2025-11-19", 120));
        events.add(new Event("Montmartre Music Festival", "Paris", "2025-08-05", 18));
        events.add(new Event("Holiday Market Opening", "Paris", "2025-12-06", 0));

        passes.add(new Pass("Paris Museum Pass", "Paris", 2, 89));
        passes.add(new Pass("Paris City Pass", "Paris", 3, 119));
        passes.add(new Pass("Seine Cruise & Museum Combo", "Paris", 1, 55));
        passes.add(new Pass("Versailles & Louvre Pass", "Paris", 1, 95));
        passes.add(new Pass("Family Attractions Pack", "Paris", 3, 140));
        passes.add(new Pass("Art Lover's Passport", "Paris", 4, 160));
        passes.add(new Pass("Gourmet Dining Pass", "Paris", 1, 85));
        passes.add(new Pass("Paris Nightlife Pass", "Paris", 1, 60));
        passes.add(new Pass("Transport & Museum Bundle", "Paris", 3, 110));
        passes.add(new Pass("Historic Landmarks Pass", "Paris", 2, 75));
        passes.add(new Pass("Student Saver Paris", "Paris", 2, 40));
        passes.add(new Pass("Premium Access Pass", "Paris", 5, 220));
        passes.add(new Pass("Romantic Weekend Pass", "Paris", 2, 170));
        passes.add(new Pass("Seine & Sightseeing Combo", "Paris", 1, 65));
        passes.add(new Pass("Weekend Culture Pass", "Paris", 3, 95));
        passes.add(new Pass("Local's Choice Pass", "Paris", 4, 130));
        passes.add(new Pass("Paris Explorer Lite", "Paris", 2, 55));
        passes.add(new Pass("Museum Marathon Pass", "Paris", 7, 180));
        passes.add(new Pass("Galleries & Exhibitions Pass", "Paris", 5, 150));
        passes.add(new Pass("Metro + Attractions Pass", "Paris", 3, 99));

        tours.add(new Tour("Seine River Cruise", "Paris", "1h", 34));
        tours.add(new Tour("Versailles Day Trip", "Paris", "6h", 120));
        tours.add(new Tour("Montmartre Food & Wine Tour", "Paris", "3h", 65));
        tours.add(new Tour("Eiffel Tower Summit Tour", "Paris", "1.5h", 45));
        tours.add(new Tour("Louvre Highlights Tour", "Paris", "2h", 55));
        tours.add(new Tour("Private Classic Paris Tour", "Paris", "3h", 220));
        tours.add(new Tour("Paris Bike Sightseeing", "Paris", "3h", 38));
        tours.add(new Tour("Photography Tour of Paris", "Paris", "2h", 40));
        tours.add(new Tour("Wine Tasting in Montparnasse", "Paris", "2.5h", 75));
        tours.add(new Tour("Hidden Gems Walking Tour", "Paris", "2h", 28));
        tours.add(new Tour("Night Lights River Cruise", "Paris", "1.5h", 48));
        tours.add(new Tour("Gourmet Market Tour", "Paris", "2.5h", 60));
        tours.add(new Tour("Historical Quarter Walk", "Paris", "2h", 22));
        tours.add(new Tour("Luxury Private Tour", "Paris", "4h", 320));
        tours.add(new Tour("Family Adventure Day", "Paris", "5h", 95));
        tours.add(new Tour("Guided Louvre Evening", "Paris", "1.5h", 50));
        tours.add(new Tour("Romantic Sunset Cruise", "Paris", "1h", 42));
        tours.add(new Tour("Vintage Car City Tour", "Paris", "2h", 140));
        tours.add(new Tour("Culinary Masterclass Tour", "Paris", "3h", 120));
        tours.add(new Tour("Hidden Passageways Tour", "Paris", "1.5h", 26));

        // ----------------- NEW YORK (USA) -----------------
        hotels.add(new Hotel("The Plaza", "New York", 5, 950, "Historic luxury hotel on Fifth Avenue"));
        hotels.add(new Hotel("The St. Regis New York", "New York", 5, 820, "Prestigious luxury near Central Park"));
        hotels.add(new Hotel("Four Seasons New York", "New York", 5, 780, "Iconic luxury in Midtown"));
        hotels.add(new Hotel("The Standard High Line", "New York", 4, 350, "Hip hotel with skyline views"));
        hotels.add(new Hotel("Moxy NYC Times Square", "New York", 3, 140, "Affordable stylish option in the theatre district"));
        hotels.add(new Hotel("The Langham, New York", "New York", 5, 600, "Luxury along Bryant Park"));
        hotels.add(new Hotel("Arlo NoMad", "New York", 4, 210, "Compact modern rooms in NoMad"));
        hotels.add(new Hotel("Hilton Midtown", "New York", 4, 240, "Convenient for Broadway and business"));
        hotels.add(new Hotel("Le Parker Meridien", "New York", 5, 420, "Wellness-focused hotel near Central Park"));
        hotels.add(new Hotel("The Ludlow", "New York", 4, 260, "Edgy boutique in Lower East Side"));
        hotels.add(new Hotel("The Peninsula New York", "New York", 5, 700, "Classic luxury near Fifth Avenue"));
        hotels.add(new Hotel("Ace Hotel New York", "New York", 4, 190, "Trendy option in Midtown"));
        hotels.add(new Hotel("The Greenwich Hotel", "New York", 5, 520, "Boutique luxury in Tribeca"));
        hotels.add(new Hotel("The William Vale", "New York", 4, 300, "Modern in Williamsburg with skyline views"));
        hotels.add(new Hotel("NoMad Hotel", "New York", 5, 450, "Luxurious and central"));
        hotels.add(new Hotel("Hotel Beacon", "New York", 4, 220, "Comfortable upper west side hotel"));
        hotels.add(new Hotel("citizenM New York Times Square", "New York", 4, 160, "Modern compact rooms"));
        hotels.add(new Hotel("Pod 39", "New York", 3, 110, "Budget-friendly with rooftop bar"));
        hotels.add(new Hotel("The Plaza Athénée (NY sample)", "New York", 5, 870, "Luxury sample property"));
        hotels.add(new Hotel("InterContinental New York", "New York", 5, 499, "Upscale hotel near Times Square"));

        restaurants.add(new Restaurant("Le Bernardin (sample)", "New York", "Seafood", 175));
        restaurants.add(new Restaurant("Eleven Madison Park", "New York", "Contemporary", 295));
        restaurants.add(new Restaurant("Katz's Delicatessen", "New York", "Deli", 22));
        restaurants.add(new Restaurant("Joe's Pizza", "New York", "Pizza", 18));
        restaurants.add(new Restaurant("Peter Luger Steak House", "New York", "Steakhouse", 85));
        restaurants.add(new Restaurant("Shake Shack (Madison Square)", "New York", "Burgers", 15));
        restaurants.add(new Restaurant("Momofuku Noodle Bar", "New York", "Asian Fusion", 45));
        restaurants.add(new Restaurant("Per Se (sample)", "New York", "Fine Dining", 375));
        restaurants.add(new Restaurant("The Halal Guys", "New York", "Street Food", 12));
        restaurants.add(new Restaurant("Gramercy Tavern", "New York", "American", 110));
        restaurants.add(new Restaurant("Balthazar", "New York", "French Bistro", 65));
        restaurants.add(new Restaurant("Blue Hill", "New York", "Farm-to-Table", 125));
        restaurants.add(new Restaurant("Smorgasburg (Brooklyn)", "New York", "Market", 10));
        restaurants.add(new Restaurant("Di Fara Pizza", "New York", "Pizza", 25));
        restaurants.add(new Restaurant("Lilia", "New York", "Italian", 70));
        restaurants.add(new Restaurant("Russ & Daughters Cafe", "New York", "Cafe", 28));
        restaurants.add(new Restaurant("Carbone", "New York", "Italian-American", 120));
        restaurants.add(new Restaurant("Roberta's Pizza", "New York", "Pizza", 22));
        restaurants.add(new Restaurant("ABC Kitchen", "New York", "Modern American", 95));
        restaurants.add(new Restaurant("The River Café", "New York", "Fine Dining", 150));

        cars.add(new Car("Hertz", "Toyota Camry", "New York", "Sedan", 75));
        cars.add(new Car("Enterprise", "Chevrolet Malibu", "New York", "Sedan", 72));
        cars.add(new Car("Avis", "Nissan Altima", "New York", "Sedan", 70));
        cars.add(new Car("Zipcar", "Honda Civic", "New York", "Compact", 35));
        cars.add(new Car("Sixt", "BMW 5 Series", "New York", "Premium", 140));
        cars.add(new Car("Budget", "Ford Escape", "New York", "SUV", 95));
        cars.add(new Car("Alamo", "Kia Sportage", "New York", "SUV", 92));
        cars.add(new Car("Avis", "Chevrolet Tahoe", "New York", "Large SUV", 150));
        cars.add(new Car("Enterprise", "Dodge Grand Caravan", "New York", "Van", 130));
        cars.add(new Car("Hertz", "Toyota Prius", "New York", "Hybrid", 80));
        cars.add(new Car("Sixt", "Audi A6", "New York", "Premium", 145));
        cars.add(new Car("Zipcar", "Mini Cooper", "New York", "Mini", 40));
        cars.add(new Car("Budget", "Hyundai Elantra", "New York", "Compact", 60));
        cars.add(new Car("Avis", "Ford Transit", "New York", "Van", 135));
        cars.add(new Car("Enterprise", "Jeep Wrangler", "New York", "SUV", 110));
        cars.add(new Car("Hertz", "Subaru Outback", "New York", "Wagon", 95));
        cars.add(new Car("Sixt", "Mercedes S-Class", "New York", "Luxury", 260));
        cars.add(new Car("Alamo", "Toyota RAV4", "New York", "SUV", 98));
        cars.add(new Car("Avis", "Kia Optima", "New York", "Sedan", 68));
        cars.add(new Car("Budget", "Chevrolet Spark", "New York", "Mini", 44));

        attractions.add(new Attraction("Statue of Liberty", "New York", "Landmark", 22));
        attractions.add(new Attraction("Central Park", "New York", "Park", 0));
        attractions.add(new Attraction("Metropolitan Museum of Art", "New York", "Museum", 25));
        attractions.add(new Attraction("Empire State Building", "New York", "Observation", 44));
        attractions.add(new Attraction("Times Square", "New York", "District", 0));
        attractions.add(new Attraction("Brooklyn Bridge Walk", "New York", "Walking", 0));
        attractions.add(new Attraction("High Line Park", "New York", "Park", 0));
        attractions.add(new Attraction("One World Observatory", "New York", "Observation", 38));
        attractions.add(new Attraction("MoMA", "New York", "Museum", 25));
        attractions.add(new Attraction("Broadway Show (sample)", "New York", "Theatre", 120));
        attractions.add(new Attraction("Coney Island Boardwalk", "New York", "Beach", 0));
        attractions.add(new Attraction("Rockefeller Center", "New York", "Landmark", 0));
        attractions.add(new Attraction("The Frick Collection", "New York", "Museum", 22));
        attractions.add(new Attraction("Grand Central Terminal", "New York", "Historic", 0));
        attractions.add(new Attraction("American Museum of Natural History", "New York", "Museum", 23));
        attractions.add(new Attraction("Intrepid Sea, Air & Space Museum", "New York", "Museum", 33));
        attractions.add(new Attraction("Greenwich Village Walk", "New York", "Neighborhood", 0));
        attractions.add(new Attraction("Chelsea Market", "New York", "Market", 0));
        attractions.add(new Attraction("St. Patrick's Cathedral", "New York", "Religious", 0));
        attractions.add(new Attraction("Cultural Festival (sample)", "New York", "Event", 15));

        events.add(new Event("Macy's Thanksgiving Parade (sample)", "New York", "2025-11-27", 0));
        events.add(new Event("New Year's Eve Times Square (sample)", "New York", "2025-12-31", 0));
        events.add(new Event("US Open Tennis (sample)", "New York", "2025-08-25", 120));
        events.add(new Event("SummerStage Concert", "New York", "2025-07-10", 35));
        events.add(new Event("Tribeca Film Festival (sample)", "New York", "2025-06-05", 60));
        events.add(new Event("Fleet Week (sample)", "New York", "2025-05-24", 0));
        events.add(new Event("Broadway Week", "New York", "2025-10-14", 40));
        events.add(new Event("Rooftop Food Festival", "New York", "2025-08-16", 28));
        events.add(new Event("Jazz at Lincoln Center", "New York", "2025-09-20", 55));
        events.add(new Event("Holiday Market Opening", "New York", "2025-11-29", 0));
        events.add(new Event("Outdoor Film Series", "New York", "2025-07-02", 12));
        events.add(new Event("Brooklyn Book Festival", "New York", "2025-09-06", 8));
        events.add(new Event("Oyster Festival", "New York", "2025-06-21", 22));
        events.add(new Event("Cultural Parade", "New York", "2025-08-03", 0));
        events.add(new Event("NYC Marathon (sample)", "New York", "2025-11-02", 80));
        events.add(new Event("Food Truck Rally", "New York", "2025-05-18", 10));
        events.add(new Event("Open Air Opera Night", "New York", "2025-07-29", 75));
        events.add(new Event("Indie Music Fair", "New York", "2025-06-12", 20));
        events.add(new Event("Artisan Craft Fair", "New York", "2025-09-13", 6));
        events.add(new Event("Summer Street Festival", "New York", "2025-08-22", 18));

        passes.add(new Pass("New York CityPASS", "New York", 5, 136));
        passes.add(new Pass("New York Explorer Pass", "New York", 3, 99));
        passes.add(new Pass("Museum Mile Pass", "New York", 2, 50));
        passes.add(new Pass("Broadway + Attractions Pass", "New York", 2, 160));
        passes.add(new Pass("Statue & Museum Combo", "New York", 1, 59));
        passes.add(new Pass("City Highlights Pass", "New York", 3, 115));
        passes.add(new Pass("Family Fun Pack", "New York", 3, 140));
        passes.add(new Pass("Museum Hopper Pass", "New York", 4, 180));
        passes.add(new Pass("Transit + Attractions", "New York", 3, 99));
        passes.add(new Pass("NYC Nightlife Pass", "New York", 1, 55));
        passes.add(new Pass("Kids Adventure Pass", "New York", 2, 60));
        passes.add(new Pass("Premium Access Pass", "New York", 5, 220));
        passes.add(new Pass("Cultural Circuit Pass", "New York", 3, 125));
        passes.add(new Pass("Outdoor Explorer Pass", "New York", 2, 48));
        passes.add(new Pass("Foodie Delight Pass", "New York", 1, 85));
        passes.add(new Pass("Art & History Pass", "New York", 4, 150));
        passes.add(new Pass("Museum & Cruise Bundle", "New York", 2, 110));
        passes.add(new Pass("Student Discount Pass", "New York", 2, 45));
        passes.add(new Pass("Premium Family Pass", "New York", 4, 195));
        passes.add(new Pass("Local Experience Bundle", "New York", 2, 89));

        tours.add(new Tour("Statue of Liberty & Ellis Island", "New York", "3h", 45));
        tours.add(new Tour("Broadway Behind the Scenes", "New York", "2h", 55));
        tours.add(new Tour("Central Park Pedicab Tour", "New York", "1h", 32));
        tours.add(new Tour("Lower Manhattan Walking Tour", "New York", "2h", 25));
        tours.add(new Tour("Brooklyn Bridge & DUMBO Tour", "New York", "2h", 20));
        tours.add(new Tour("NYC Food & Neighborhood Tour", "New York", "3h", 65));
        tours.add(new Tour("Harbor Cruise & Skyline", "New York", "1.5h", 35));
        tours.add(new Tour("Historic Harlem Gospel & Food Tour", "New York", "3h", 48));
        tours.add(new Tour("High Line & Chelsea Market Tour", "New York", "2h", 22));
        tours.add(new Tour("Private Helicopter Tour (sample)", "New York", "12m", 450));
        tours.add(new Tour("Museum Highlights Guided Tour", "New York", "2h", 40));
        tours.add(new Tour("Photography Walk of Manhattan", "New York", "2h", 30));
        tours.add(new Tour("Rooftop Bars Crawl", "New York", "3h", 55));
        tours.add(new Tour("Wall Street & Financial District Tour", "New York", "1.5h", 20));
        tours.add(new Tour("Architectural Midtown Tour", "New York", "2h", 28));
        tours.add(new Tour("Hudson River Bike Tour", "New York", "3h", 35));
        tours.add(new Tour("Gotham History Tour", "New York", "2h", 24));
        tours.add(new Tour("Culinary Market Tour", "New York", "2.5h", 48));
        tours.add(new Tour("Sunset Sail Cruise", "New York", "1.5h", 50));
        tours.add(new Tour("Hidden Bars & Speakeasy Tour", "New York", "3h", 60));
    }

    // ---------- Public getters with filtering ----------
    public static List<Hotel> getHotelsForCity(String city) {
        if (city == null) return new ArrayList<>();
        return hotels.stream().filter(h -> h.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    public static List<Restaurant> getRestaurantsForCity(String city) {
        if (city == null) return new ArrayList<>();
        return restaurants.stream().filter(r -> r.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    public static List<Car> getCarsForCity(String city) {
        if (city == null) return new ArrayList<>();
        return cars.stream().filter(c -> c.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    public static List<Attraction> getAttractionsForCity(String city) {
        if (city == null) return new ArrayList<>();
        return attractions.stream().filter(a -> a.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    public static List<Event> getEventsForCity(String city) {
        if (city == null) return new ArrayList<>();
        return events.stream().filter(e -> e.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    public static List<Pass> getPassesForCity(String city) {
        if (city == null) return new ArrayList<>();
        return passes.stream().filter(p -> p.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    public static List<Tour> getToursForCity(String city) {
        if (city == null) return new ArrayList<>();
        return tours.stream().filter(t -> t.city.equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    // ---------- Utility sorting helpers ----------
    public static <T> List<T> sortAZ(List<T> list, java.util.function.Function<T,String> key) {
        return list.stream().sorted(Comparator.comparing(key, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
    }
    public static <T> List<T> sortZA(List<T> list, java.util.function.Function<T,String> key) {
        return list.stream().sorted(Comparator.comparing(key, String.CASE_INSENSITIVE_ORDER).reversed()).collect(Collectors.toList());
    }
    public static <T> List<T> sortPriceAsc(List<T> list, java.util.function.ToDoubleFunction<T> key) {
        return list.stream().sorted(Comparator.comparingDouble(key)).collect(Collectors.toList());
    }
    public static <T> List<T> sortPriceDesc(List<T> list, java.util.function.ToDoubleFunction<T> key) {
        return list.stream().sorted(Comparator.comparingDouble(key).reversed()).collect(Collectors.toList());
    }
}

