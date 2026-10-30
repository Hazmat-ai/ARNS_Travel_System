package arns;

import static arns.DesktopSupport.*;

import arns.data.*;
import arns.model.*;
import arns.service.*;
import arns.ui.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.nio.file.*;
import java.time.Clock;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Test-only UI driver using a fresh in-memory database; never shipped in the runnable JAR. */
public final class DesktopDemo {
  public static void main(String[] args) throws Exception {
    Path output = Path.of(args.length == 0 ? "target/demo" : args[0]);
    Files.createDirectories(output);
    var db = Database.memory();
    var catalog = new CatalogService();
    var service =
        new TravelService(catalog, new ReservationRepository(db, catalog.all()), Clock.systemUTC());
    TravelWindow window =
        edt(
            () -> {
              FlatLightLaf.setup();
              var w = new TravelWindow(service, db);
              w.setLocation(40, 40);
              w.setVisible(true);
              return w;
            });
    await(() -> !window.isBusy());
    Robot robot = new Robot();
    robot.setAutoDelay(90);
    Thread.sleep(1000);
    capture(window, robot, output, "01-explore");
    edt(
        () -> {
          ((JComboBox<?>) named(window, "destination")).setSelectedItem("Paris");
          ((JComboBox<?>) named(window, "category")).setSelectedItem(Category.HOTELS);
          return null;
        });
    Thread.sleep(1300);
    capture(window, robot, output, "02-hotels");
    click(window, "addToPlan");
    var form = dialog("Add to trip plan");
    edt(
        () -> {
          ((JTextField) named(form, "travellerName")).setText("Alex Demo");
          ((JTextField) named(form, "travellerEmail")).setText("alex@example.com");
          ((JSpinner) named(form, "quantity")).setValue(3);
          return null;
        });
    Thread.sleep(1500);
    capture(window, robot, output, "03-draft");
    click(form, "saveDraft");
    await(() -> !form.isShowing() && !window.isBusy());
    click(window, "planNavigation");
    await(() -> !window.isBusy());
    Thread.sleep(1700);
    capture(window, robot, output, "04-plan");
    edt(
        () -> {
          ((JTable) named(window, "planTable")).setRowSelectionInterval(0, 0);
          return null;
        });
    click(window, "editDraft");
    var edit = dialog("Edit planning draft");
    edt(
        () -> {
          ((JSpinner) named(edit, "quantity")).setValue(2);
          return null;
        });
    Thread.sleep(1100);
    click(edit, "saveDraft");
    await(() -> !edit.isShowing() && !window.isBusy());
    Thread.sleep(900);
    edt(
        () -> {
          ((JTable) named(window, "planTable")).setRowSelectionInterval(0, 0);
          return null;
        });
    click(window, "requestDraft");
    dialog("Review action");
    Thread.sleep(1200);
    answer("Yes");
    await(() -> !window.isBusy() && status(service) == Reservation.Status.REQUESTED);
    Thread.sleep(1400);
    capture(window, robot, output, "05-follow-up");
    edt(
        () -> {
          window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
          return null;
        });
    System.out.println(
        "Desktop demo captured from the actual Swing application with a temporary store.");
    System.exit(0);
  }

  private static void capture(TravelWindow window, Robot robot, Path output, String name)
      throws Exception {
    Rectangle bounds = edt(window::getBounds);
    ImageIO.write(robot.createScreenCapture(bounds), "png", output.resolve(name + ".png").toFile());
  }

  private static Reservation.Status status(TravelService service) {
    try {
      return service.reservations().get(0).status();
    } catch (java.sql.SQLException e) {
      throw new AssertionError(e);
    }
  }
}
