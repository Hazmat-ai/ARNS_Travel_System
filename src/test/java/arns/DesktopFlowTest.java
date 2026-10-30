package arns;

import static arns.DesktopSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import arns.data.*;
import arns.model.*;
import arns.service.*;
import arns.ui.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.time.Clock;
import javax.swing.*;
import org.junit.jupiter.api.Test;

class DesktopFlowTest {
  @Test
  void realDesktopCanSaveEditFollowUpAndCancel() throws Exception {
    if (Boolean.getBoolean("arns.requireDesktop")) {
      assertFalse(GraphicsEnvironment.isHeadless(), "Desktop validation was explicitly required.");
    } else {
      assumeFalse(GraphicsEnvironment.isHeadless(), "Run under a desktop display or Xvfb.");
    }
    Database db = Database.memory();
    CatalogService catalog = new CatalogService();
    var repository = new ReservationRepository(db, catalog.all());
    var service = new TravelService(catalog, repository, Clock.systemUTC());
    TravelWindow window =
        edt(
            () -> {
              FlatLightLaf.setup();
              var view = new TravelWindow(service, db);
              view.setVisible(true);
              return view;
            });
    try {
      await(() -> !window.isBusy());
      edt(
          () -> {
            ((JComboBox<?>) named(window, "destination")).setSelectedItem("Paris");
            ((JComboBox<?>) named(window, "category")).setSelectedItem(Category.HOTELS);
            return null;
          });
      click(window, "addToPlan");
      JDialog form = dialog("Add to trip plan");
      edt(
          () -> {
            ((JTextField) named(form, "travellerName")).setText("Alex Demo");
            ((JTextField) named(form, "travellerEmail")).setText("alex@example.com");
            ((JSpinner) named(form, "quantity")).setValue(3);
            return null;
          });
      click(form, "saveDraft");
      await(() -> !form.isShowing() && !window.isBusy());
      assertEquals(1, service.reservations().size());
      assertEquals(3, service.reservations().get(0).units());
      click(window, "planNavigation");
      await(() -> !window.isBusy());
      edt(
          () -> {
            ((JTable) named(window, "planTable")).setRowSelectionInterval(0, 0);
            return null;
          });
      click(window, "editDraft");
      JDialog edit = dialog("Edit planning draft");
      edt(
          () -> {
            ((JSpinner) named(edit, "quantity")).setValue(2);
            return null;
          });
      click(edit, "saveDraft");
      await(() -> !edit.isShowing() && !window.isBusy());
      assertEquals(2, service.reservations().get(0).units());
      edt(
          () -> {
            ((JTable) named(window, "planTable")).setRowSelectionInterval(0, 0);
            return null;
          });
      click(window, "requestDraft");
      dialog("Review action");
      answer("Yes");
      await(() -> !window.isBusy() && serviceStatus(service) == Reservation.Status.REQUESTED);
      edt(
          () -> {
            ((JTable) named(window, "planTable")).setRowSelectionInterval(0, 0);
            return null;
          });
      click(window, "cancelRequest");
      dialog("Review action");
      answer("Yes");
      await(() -> !window.isBusy() && serviceStatus(service) == Reservation.Status.CANCELLED);
      assertEquals(0, TravelService.total(service.reservations()).cents());
    } finally {
      edt(
          () -> {
            for (Window dialog : Window.getWindows())
              if (dialog instanceof JDialog) dialog.dispose();
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            return null;
          });
      db.close();
    }
  }

  private static Reservation.Status serviceStatus(TravelService service) {
    try {
      return service.reservations().get(0).status();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
