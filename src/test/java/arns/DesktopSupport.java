package arns;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.BooleanSupplier;
import javax.swing.*;

public final class DesktopSupport {
  private DesktopSupport() {}

  public static <T> T edt(Callable<T> job) throws Exception {
    if (SwingUtilities.isEventDispatchThread()) return job.call();
    FutureTask<T> task = new FutureTask<>(job);
    SwingUtilities.invokeAndWait(task);
    return task.get();
  }

  public static Component named(Container root, String name) {
    for (Component child : root.getComponents()) {
      if (name.equals(child.getName())) return child;
      if (child instanceof Container container) {
        Component found = named(container, name);
        if (found != null) return found;
      }
    }
    return null;
  }

  public static void click(Container root, String name) throws Exception {
    SwingUtilities.invokeLater(() -> ((AbstractButton) named(root, name)).doClick());
    edt(() -> null);
  }

  public static void await(BooleanSupplier ready) throws Exception {
    long deadline = System.nanoTime() + 8_000_000_000L;
    while (System.nanoTime() < deadline) {
      if (edt(ready::getAsBoolean)) return;
      Thread.sleep(30);
    }
    throw new AssertionError("Desktop action timed out.");
  }

  public static JDialog dialog(String title) throws Exception {
    final JDialog[] result = {null};
    await(
        () -> {
          for (Window w : Window.getWindows())
            if (w instanceof JDialog d && d.isShowing() && d.getTitle().equals(title)) {
              result[0] = d;
              return true;
            }
          return false;
        });
    return result[0];
  }

  public static void answer(String text) {
    SwingUtilities.invokeLater(
        () -> {
          for (Window w : Window.getWindows())
            if (w instanceof JDialog d && d.isShowing() && d.getTitle().equals("Review action")) {
              findButton(d, text).doClick();
              return;
            }
        });
  }

  private static JButton findButton(Container root, String text) {
    for (Component c : root.getComponents()) {
      if (c instanceof JButton b && text.equals(b.getText())) return b;
      if (c instanceof Container group) {
        JButton found = findButton(group, text);
        if (found != null) return found;
      }
    }
    return null;
  }
}
