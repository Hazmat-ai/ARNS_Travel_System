package arns;

import static org.junit.jupiter.api.Assertions.*;

import arns.model.*;
import arns.service.CatalogService;
import arns.ui.*;
import java.time.LocalDate;
import javax.swing.*;
import org.junit.jupiter.api.Test;

class BookingFormTest {
  @Test
  void formUsesTypedQuantityAndAccessibleControls() throws Exception {
    SwingUtilities.invokeAndWait(
        () -> {
          var item = new CatalogService().search("London", Category.HOTELS, "", false).get(0);
          var form = new BookingForm(item, item.price(), LocalDate.of(2026, 11, 15), null);
          assertEquals("travellerName", form.name.getAccessibleContext().getAccessibleName());
          assertEquals(LocalDate.of(2026, 11, 15), form.travelDate());
          form.units.setValue(3);
          assertEquals(3, form.quantity());
          form.date.setText("not-a-date");
          assertThrows(IllegalArgumentException.class, form::travelDate);
          assertEquals(Boolean.TRUE, form.error.getClientProperty("html.disable"));
        });
  }
}
