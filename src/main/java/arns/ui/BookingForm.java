package arns.ui;

import arns.model.*;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/** Reusable form with typed values and explicit pricing units. */
public final class BookingForm extends JPanel {
  private static final long serialVersionUID = 1L;
  public final JTextField name = new JTextField(),
      email = new JTextField(),
      date = new JTextField();
  public final JSpinner units = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
  public final JLabel error = Theme.label(" ", 12, new Color(0xA83F32));
  public final JButton save = Theme.button("Save draft", true),
      cancel = Theme.button("Cancel", false);
  private final JLabel total = Theme.label("", 22, Theme.GREEN);
  private final Money price;

  public BookingForm(CatalogItem item, Money price, LocalDate initialDate, Reservation existing) {
    this.price = price;
    setLayout(new BorderLayout(0, 20));
    setBackground(Theme.WHITE);
    setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
    JTextArea heading = Theme.paragraph(item.title(), 24);
    heading.setForeground(Theme.INK);
    add(heading, BorderLayout.NORTH);
    JPanel fields = Theme.panel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(3, 0, 9, 0);
    addField(fields, c, "Traveller / organiser", name, "travellerName");
    addField(fields, c, "Email address", email, "travellerEmail");
    addField(fields, c, "Travel date (YYYY-MM-DD)", date, "travelDate");
    addField(fields, c, "Quantity · " + item.category().unit(), units, "quantity");
    if (existing != null) {
      name.setText(existing.traveller());
      email.setText(existing.email());
      units.setValue(existing.units());
      date.setText(existing.date().toString());
    } else date.setText(initialDate.toString());
    c.gridy++;
    fields.add(
        Theme.label(
            "Illustrative rate: " + price.display() + " " + item.category().unit(),
            13,
            Theme.MUTED),
        c);
    c.gridy++;
    fields.add(total, c);
    c.gridy++;
    fields.add(error, c);
    add(fields, BorderLayout.CENTER);
    JPanel bottom = Theme.panel(new BorderLayout(0, 14));
    bottom.add(
        Theme.paragraph(
            "Saved locally as a planning draft. No payment is collected and no supplier reservation"
                + " is made. All estimates use CAD.",
            12),
        BorderLayout.CENTER);
    JPanel actions = Theme.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    actions.add(cancel);
    actions.add(save);
    bottom.add(actions, BorderLayout.SOUTH);
    add(bottom, BorderLayout.SOUTH);
    ChangeListener update = e -> updateTotal();
    units.addChangeListener(update);
    updateTotal();
    Theme.accessible(save, "saveDraft");
    Theme.accessible(cancel, "cancelDraft");
  }

  private void addField(
      JPanel panel, GridBagConstraints c, String label, JComponent field, String name) {
    JLabel l = Theme.label(label, 13, Theme.INK);
    l.setLabelFor(field);
    panel.add(l, c);
    c.gridy++;
    panel.add(field, c);
    c.gridy++;
    Theme.accessible(field, name);
  }

  public int quantity() {
    try {
      units.commitEdit();
    } catch (java.text.ParseException e) {
      throw new IllegalArgumentException("Enter a whole-number quantity.");
    }
    return ((Number) units.getValue()).intValue();
  }

  public LocalDate travelDate() {
    try {
      return LocalDate.parse(date.getText().strip());
    } catch (java.time.format.DateTimeParseException e) {
      throw new IllegalArgumentException("Enter the travel date as YYYY-MM-DD.");
    }
  }

  private void updateTotal() {
    try {
      total.setText(
          "Estimate: " + price.multiply(((Number) units.getValue()).intValue()).display());
    } catch (RuntimeException e) {
      total.setText("Estimate is out of range.");
    }
  }
}
