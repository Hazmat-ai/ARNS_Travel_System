package arns.ui;

import arns.data.Database;
import arns.model.*;
import arns.service.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public final class TravelWindow extends JFrame {
  private static final long serialVersionUID = 1L;
  private final TravelService service;
  private final Database database;
  private final ExecutorService executor =
      Executors.newSingleThreadExecutor(
          r -> {
            Thread t = new Thread(r, "arns-store");
            t.setDaemon(true);
            return t;
          });
  private final CardLayout cards = new CardLayout();
  private final JPanel pages = Theme.panel(cards);
  private final JLabel status = Theme.label("Opening your travel desk…", 12, Theme.MUTED);
  private final JLabel planTotal = Theme.label("", 18, Theme.GREEN);
  private final JComboBox<String> city =
      new JComboBox<>(CatalogService.CITIES.toArray(String[]::new));
  private final JComboBox<Category> category = new JComboBox<>(Category.values());
  private final JComboBox<String> sort =
      new JComboBox<>(new String[] {"Name A–Z", "Estimate low–high"});
  private final JTextField search = new JTextField(18);
  private final DefaultTableModel catalogModel =
      model(new String[] {"Travel option", "Category", "Rate (CAD)", "Unit"});
  private final DefaultTableModel planModel =
      model(new String[] {"Item", "City", "Date", "Qty", "Estimate (CAD)", "Stage", "Traveller"});
  private final JTable catalogTable = table(catalogModel, "catalogTable"),
      planTable = table(planModel, "planTable");
  private final JTextArea detail =
      Theme.paragraph("Choose an option to see its planning details.", 14);
  private final JLabel rate = Theme.label("", 23, Theme.GREEN);
  private final JButton add = Theme.button("Add to trip plan", true);
  private final JComboBox<String> stage =
      new JComboBox<>(new String[] {"All stages", "Drafts", "Follow-up", "Cancelled"});
  private List<CatalogItem> catalogRows = List.of();
  private List<Reservation> allRows = List.of(), planRows = List.of();
  private volatile boolean busy;

  public TravelWindow(TravelService service, Database database) {
    super("ARNS · Your travel desk");
    this.service = service;
    this.database = database;
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setSize(1240, 820);
    setMinimumSize(new Dimension(1040, 700));
    setLayout(new BorderLayout());
    getContentPane().setBackground(Theme.PAPER);
    add(sidebar(), BorderLayout.WEST);
    JPanel body = new JPanel(new BorderLayout(0, 18));
    body.setBackground(Theme.PAPER);
    body.setBorder(new EmptyBorder(28, 30, 18, 30));
    JPanel heading = new JPanel(new BorderLayout());
    heading.setOpaque(false);
    JLabel title = Theme.label("Plan your next trip.", 30, Theme.INK);
    title.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
    heading.add(title, BorderLayout.NORTH);
    heading.add(
        Theme.label(
            "Demo catalog · illustrative CAD estimates · no supplier booking or payment",
            12,
            Theme.MUTED),
        BorderLayout.SOUTH);
    body.add(heading, BorderLayout.NORTH);
    pages.add(explore(), "explore");
    pages.add(plan(), "plan");
    body.add(pages, BorderLayout.CENTER);
    body.add(status, BorderLayout.SOUTH);
    add(body, BorderLayout.CENTER);
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            if (busy) {
              message("Wait for the current operation to finish before closing.");
              return;
            }
            executor.shutdown();
            dispose();
            try {
              database.close();
            } catch (Exception ignored) {
              System.err.println("ARNS: database close failed.");
            }
          }
        });
    city.addActionListener(e -> filterCatalog());
    category.addActionListener(e -> filterCatalog());
    sort.addActionListener(e -> filterCatalog());
    search
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              public void insertUpdate(DocumentEvent e) {
                filterCatalog();
              }

              public void removeUpdate(DocumentEvent e) {
                filterCatalog();
              }

              public void changedUpdate(DocumentEvent e) {
                filterCatalog();
              }
            });
    stage.addActionListener(e -> showPlanRows());
    catalogTable.getSelectionModel().addListSelectionListener(e -> showDetail());
    add.addActionListener(
        e -> {
          int selected = catalogTable.getSelectedRow();
          if (selected >= 0)
            openForm(catalogRows.get(catalogTable.convertRowIndexToModel(selected)), null);
        });
    filterCatalog();
    refresh();
    setLocationRelativeTo(null);
  }

  private JPanel sidebar() {
    JPanel rail = new JPanel(new BorderLayout());
    rail.setBackground(Theme.GREEN);
    rail.setPreferredSize(new Dimension(190, 0));
    rail.setBorder(new EmptyBorder(32, 20, 24, 20));
    JPanel top = new JPanel();
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
    top.setOpaque(false);
    JLabel brand = Theme.label("ARNS", 29, Color.WHITE);
    brand.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 29));
    top.add(brand);
    top.add(Theme.label("TRAVEL DESK", 11, new Color(0xC2D5CC)));
    top.add(Box.createVerticalStrut(42));
    JButton explore = Theme.button("Explore options", false),
        plan = Theme.button("Your trip plan", false);
    explore.setAlignmentX(0);
    plan.setAlignmentX(0);
    explore.addActionListener(e -> cards.show(pages, "explore"));
    plan.addActionListener(
        e -> {
          cards.show(pages, "plan");
          refresh();
        });
    top.add(explore);
    top.add(Box.createVerticalStrut(12));
    top.add(plan);
    rail.add(top, BorderLayout.NORTH);
    JTextArea note =
        Theme.paragraph(
            "Local planning workspace\n\n"
                + "Your drafts stay on this computer. Export a backup to keep a separate copy.",
            12);
    note.setForeground(new Color(0xD2DFD8));
    note.setFocusable(false);
    rail.add(note, BorderLayout.SOUTH);
    Theme.accessible(explore, "exploreNavigation");
    Theme.accessible(plan, "planNavigation");
    return rail;
  }

  private JPanel explore() {
    JPanel page = Theme.panel(new BorderLayout(0, 20));
    page.setBorder(new EmptyBorder(20, 20, 20, 20));
    JPanel controls = Theme.panel(new GridLayout(2, 2, 18, 12));
    controls.add(field("Destination", city));
    controls.add(field("Category", category));
    controls.add(field("Find an option", search));
    controls.add(field("Sort by", sort));
    page.add(controls, BorderLayout.NORTH);
    JPanel right = Theme.panel(new BorderLayout(0, 18));
    right.setBorder(new EmptyBorder(6, 20, 6, 0));
    JScrollPane text = new JScrollPane(detail);
    text.setBorder(null);
    right.add(text, BorderLayout.CENTER);
    JPanel bottom = Theme.panel(new BorderLayout(0, 12));
    bottom.add(rate, BorderLayout.NORTH);
    bottom.add(add, BorderLayout.SOUTH);
    right.add(bottom, BorderLayout.SOUTH);
    JSplitPane split =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(catalogTable), right);
    split.setResizeWeight(.63);
    split.setDividerLocation(510);
    split.setBorder(null);
    right.setMinimumSize(new Dimension(240, 200));
    page.add(split, BorderLayout.CENTER);
    Theme.accessible(city, "destination");
    Theme.accessible(category, "category");
    Theme.accessible(search, "catalogSearch");
    Theme.accessible(sort, "sortOptions");
    Theme.accessible(add, "addToPlan");
    return page;
  }

  private JPanel plan() {
    JPanel page = Theme.panel(new BorderLayout(0, 18));
    page.setBorder(new EmptyBorder(20, 20, 20, 20));
    JPanel heading = Theme.panel(new BorderLayout());
    heading.add(Theme.label("Your trip plan", 24, Theme.INK), BorderLayout.WEST);
    heading.add(stage, BorderLayout.EAST);
    page.add(heading, BorderLayout.NORTH);
    page.add(new JScrollPane(planTable), BorderLayout.CENTER);
    JPanel bottom = Theme.panel(new BorderLayout(0, 14));
    JPanel actions = Theme.panel(new FlowLayout(FlowLayout.LEFT, 8, 5));
    JButton edit = Theme.button("Edit draft", false),
        remove = Theme.button("Remove draft", false),
        request = Theme.button("Mark for follow-up", true),
        cancel = Theme.button("Cancel local request", false);
    actions.add(edit);
    actions.add(remove);
    actions.add(request);
    actions.add(cancel);
    edit.addActionListener(
        e -> {
          var selected = selected();
          if (selected.size() != 1) {
            message("Select one draft to edit.");
            return;
          }
          var r = selected.get(0);
          if (r.status() != Reservation.Status.DRAFT) {
            message("Only drafts can be edited.");
            return;
          }
          openForm(r.item(), r);
        });
    remove.addActionListener(
        e -> {
          var selected = selected();
          if (selected.size() != 1 || selected.get(0).status() != Reservation.Status.DRAFT) {
            message("Select one draft to remove.");
            return;
          }
          if (confirm(
              "Remove this draft? This removes its saved contact details if no other plan uses"
                  + " them."))
            work(
                () -> {
                  service.remove(selected.get(0));
                  return null;
                },
                x -> {
                  message("Draft removed.");
                  refresh();
                });
        });
    request.addActionListener(
        e -> {
          var selected = selected();
          if (selected.isEmpty()
              || selected.stream().anyMatch(r -> r.status() != Reservation.Status.DRAFT)) {
            message("Select one or more drafts for follow-up.");
            return;
          }
          if (confirm(
              "Mark these local drafts for follow-up? No request is sent to a supplier and no"
                  + " payment is collected."))
            work(
                () -> {
                  service.request(selected);
                  return null;
                },
                x -> {
                  message("Marked for supplier follow-up. No booking was made.");
                  refresh();
                });
        });
    cancel.addActionListener(
        e -> {
          var selected = selected();
          if (selected.isEmpty()
              || selected.stream().anyMatch(r -> r.status() != Reservation.Status.REQUESTED)) {
            message("Select local follow-up requests to cancel.");
            return;
          }
          if (confirm(
              "Cancel the local planning requests? This does not cancel anything with a supplier."))
            work(
                () -> {
                  service.cancel(selected);
                  return null;
                },
                x -> {
                  message("Local requests cancelled.");
                  refresh();
                });
        });
    JPanel files = Theme.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    JButton export = Theme.button("Export CSV", false),
        backup = Theme.button("Backup database", false);
    files.add(planTotal);
    files.add(export);
    files.add(backup);
    export.addActionListener(e -> export());
    backup.addActionListener(e -> backup());
    bottom.add(actions, BorderLayout.NORTH);
    bottom.add(files, BorderLayout.SOUTH);
    page.add(bottom, BorderLayout.SOUTH);
    Theme.accessible(edit, "editDraft");
    Theme.accessible(remove, "removeDraft");
    Theme.accessible(request, "requestDraft");
    Theme.accessible(cancel, "cancelRequest");
    Theme.accessible(export, "exportCsv");
    Theme.accessible(backup, "backupDatabase");
    Theme.accessible(stage, "stageFilter");
    return page;
  }

  private void filterCatalog() {
    catalogRows =
        service
            .catalog()
            .search(
                (String) city.getSelectedItem(),
                (Category) category.getSelectedItem(),
                search.getText(),
                sort.getSelectedIndex() == 1);
    catalogModel.setRowCount(0);
    for (var i : catalogRows)
      catalogModel.addRow(
          new Object[] {i.title(), i.category().label(), i.price().decimal(), i.category().unit()});
    if (!catalogRows.isEmpty()) catalogTable.setRowSelectionInterval(0, 0);
    showDetail();
  }

  private void showDetail() {
    int row = catalogTable.getSelectedRow();
    add.setEnabled(row >= 0);
    if (row < 0) {
      detail.setText("No matching options. Try a different search or category.");
      rate.setText("");
      return;
    }
    var i = catalogRows.get(catalogTable.convertRowIndexToModel(row));
    detail.setText(
        i.title()
            + "\n\n"
            + i.city()
            + " · "
            + i.category().label()
            + "\n\n"
            + i.description()
            + "\n\n"
            + "Demo planning data. Prices are illustrative CAD amounts, not supplier quotes."
            + " Confirm availability, taxes and terms independently.");
    detail.setCaretPosition(0);
    rate.setText(i.price().display() + " " + i.category().unit());
  }

  public void refresh() {
    work(
        service::reservations,
        rows -> {
          allRows = rows;
          showPlanRows();
          message(
              rows.isEmpty()
                  ? "Your plan is empty. Explore options to create your first draft."
                  : "Plan loaded · " + rows.size() + " saved item(s).");
        });
  }

  private void showPlanRows() {
    planRows =
        allRows.stream()
            .filter(
                r ->
                    stage.getSelectedIndex() == 0
                        || r.status() == Reservation.Status.values()[stage.getSelectedIndex() - 1])
            .toList();
    planModel.setRowCount(0);
    for (var r : planRows)
      planModel.addRow(
          new Object[] {
            r.item().title(),
            r.item().city(),
            r.date().toString(),
            r.units(),
            r.total().decimal(),
            stageLabel(r.status()),
            r.traveller()
          });
    planTotal.setText("Active estimate: " + TravelService.total(planRows).display());
  }

  private static String stageLabel(Reservation.Status value) {
    return switch (value) {
      case DRAFT -> "Draft";
      case REQUESTED -> "Follow-up";
      case CANCELLED -> "Cancelled";
    };
  }

  private List<Reservation> selected() {
    return Arrays.stream(planTable.getSelectedRows())
        .mapToObj(i -> planRows.get(planTable.convertRowIndexToModel(i)))
        .toList();
  }

  private void openForm(CatalogItem item, Reservation existing) {
    if (busy) {
      message("Wait for the current operation to finish.");
      return;
    }
    JDialog dialog =
        new JDialog(this, existing == null ? "Add to trip plan" : "Edit planning draft", true);
    BookingForm form =
        new BookingForm(
            item,
            existing == null ? item.price() : existing.unitPrice(),
            service.today().plusDays(14),
            existing);
    dialog.setContentPane(form);
    dialog.setSize(570, 650);
    dialog.setLocationRelativeTo(this);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    form.cancel.addActionListener(e -> dialog.dispose());
    form.save.addActionListener(
        e -> {
          try {
            String name = Checks.text(form.name.getText(), "Traveller name", 120),
                email = Checks.email(form.email.getText());
            var date = Checks.date(form.travelDate(), service.today());
            int units = form.quantity();
            form.save.setEnabled(false);
            form.cancel.setEnabled(false);
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            work(
                () ->
                    existing == null
                        ? service.add(item.id(), name, email, date, units)
                        : service.edit(existing, name, email, date, units),
                r -> {
                  dialog.dispose();
                  message("Draft saved locally.");
                  refresh();
                },
                error -> {
                  form.error.setText(error);
                  form.save.setEnabled(true);
                  form.cancel.setEnabled(true);
                  dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                });
          } catch (RuntimeException error) {
            form.error.setText(error.getMessage());
          }
        });
    dialog.getRootPane().setDefaultButton(form.save);
    dialog.setVisible(true);
  }

  private void export() {
    Path target = chooseFile("Export trip plan (contains contact details)", "arns-plan.csv");
    if (target != null)
      work(
          () -> {
            CsvExport.write(target, service.reservations());
            return null;
          },
          x -> message("CSV exported. Keep it private; it contains contact details."));
  }

  private void backup() {
    Path target = chooseFile("Save database snapshot", "arns-backup.zip");
    if (target != null)
      work(
          () -> {
            database.backup(target);
            return null;
          },
          x -> message("Database snapshot saved. Existing files were not overwritten."));
  }

  private Path chooseFile(String title, String suggested) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(title);
    chooser.setSelectedFile(new java.io.File(suggested));
    return chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION
        ? chooser.getSelectedFile().toPath()
        : null;
  }

  private boolean confirm(String message) {
    return JOptionPane.showConfirmDialog(this, message, "Review action", JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION;
  }

  private void message(String value) {
    status.setText(value);
  }

  private <T> void work(Callable<T> job, Consumer<T> success) {
    work(job, success, this::message);
  }

  private <T> void work(Callable<T> job, Consumer<T> success, Consumer<String> failure) {
    if (busy) {
      message("An operation is already in progress. Please wait.");
      return;
    }
    busy = true;
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    executor.submit(
        () -> {
          try {
            T result = job.call();
            SwingUtilities.invokeLater(
                () -> {
                  busy = false;
                  setCursor(Cursor.getDefaultCursor());
                  success.accept(result);
                });
          } catch (Exception e) {
            String safe =
                e instanceof IllegalArgumentException
                        || e instanceof IllegalStateException
                        || e instanceof java.io.IOException
                    ? Objects.requireNonNullElse(e.getMessage(), "The operation failed.")
                    : "Database operation failed. Your previous saved state is preserved; try"
                        + " reopening the app.";
            System.err.println("ARNS operation failed: " + e.getClass().getSimpleName());
            SwingUtilities.invokeLater(
                () -> {
                  busy = false;
                  setCursor(Cursor.getDefaultCursor());
                  failure.accept(safe);
                });
          }
        });
  }

  private static JPanel field(String label, JComponent value) {
    JPanel p = Theme.panel(new BorderLayout(0, 6));
    JLabel l = Theme.label(label, 12, Theme.MUTED);
    l.setLabelFor(value);
    p.add(l, BorderLayout.NORTH);
    p.add(value, BorderLayout.CENTER);
    return p;
  }

  private static DefaultTableModel model(String[] columns) {
    return new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int col) {
        return false;
      }

      @Override
      public Class<?> getColumnClass(int col) {
        for (int i = 0; i < getRowCount(); i++) {
          Object value = getValueAt(i, col);
          if (value != null) return value.getClass();
        }
        return String.class;
      }
    };
  }

  private static JTable table(DefaultTableModel model, String name) {
    JTable t = new JTable(model);
    t.setRowHeight(38);
    t.setShowGrid(false);
    t.setIntercellSpacing(new Dimension(12, 0));
    t.setSelectionBackground(new Color(0xE2ECE6));
    t.setSelectionForeground(Theme.INK);
    t.setAutoCreateRowSorter(true);
    t.setFillsViewportHeight(true);
    Theme.accessible(t, name);
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.putClientProperty("html.disable", true);
    t.setDefaultRenderer(String.class, renderer);
    return t;
  }

  public boolean isBusy() {
    return busy;
  }
}
