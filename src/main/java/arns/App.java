package arns;

import arns.data.*;
import arns.service.*;
import arns.ui.TravelWindow;
import com.formdev.flatlaf.FlatLightLaf;
import java.nio.file.Path;
import java.time.Clock;
import javax.swing.*;

public final class App {
  private App() {}

  public static void main(String[] args) {
    if (args.length > 1 || (args.length == 1 && !args[0].equals("--check"))) {
      System.err.println("Usage: java -jar arns-travel.jar [--check]");
      System.exit(2);
    }
    try {
      Path directory =
          Path.of(
              System.getProperty(
                  "arns.dataDir",
                  Path.of(System.getProperty("user.home"), ".arns-travel").toString()));
      Database database = new Database(directory);
      CatalogService catalog = new CatalogService();
      ReservationRepository repository = new ReservationRepository(database, catalog.all());
      TravelService service = new TravelService(catalog, repository, Clock.systemDefaultZone());
      if (args.length == 1 && args[0].equals("--check")) {
        System.out.println(
            "ARNS store ready. Catalog: "
                + catalog.all().size()
                + " demo options. Saved planning rows: "
                + service.reservations().size()
                + ".");
        database.close();
        return;
      }
      if (java.awt.GraphicsEnvironment.isHeadless()) {
        database.close();
        System.err.println("A desktop display is required. Use --check for a storage diagnostic.");
        return;
      }
      SwingUtilities.invokeLater(
          () -> {
            FlatLightLaf.setup();
            UIManager.put("Component.arc", 7);
            UIManager.put("Button.arc", 7);
            UIManager.put("Table.showHorizontalLines", false);
            new TravelWindow(service, database).setVisible(true);
          });
    } catch (Exception e) {
      System.err.println(
          "ARNS could not open its local store ("
              + e.getClass().getSimpleName()
              + "). Check that the data folder is writable and no other ARNS instance has it"
              + " open.");
      System.exit(1);
    }
  }
}
