package arns.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class Theme {
  public static final Color PAPER = new Color(0xF5F2EB),
      WHITE = new Color(0xFFFEFA),
      INK = new Color(0x253739),
      MUTED = new Color(0x5E6D6D),
      LINE = new Color(0xD8DFD9),
      GREEN = new Color(0x254D46);

  private Theme() {}

  public static JLabel label(String text, int size, Color color) {
    JLabel l = new JLabel(text);
    l.putClientProperty("html.disable", true);
    l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
    l.setForeground(color);
    return l;
  }

  public static JPanel panel(LayoutManager layout) {
    JPanel p = new JPanel(layout);
    p.setBackground(WHITE);
    return p;
  }

  public static JButton button(String text, boolean primary) {
    JButton b = new JButton(text);
    if (primary) {
      b.setBackground(GREEN);
      b.setForeground(Color.WHITE);
    }
    b.setMargin(new Insets(9, 14, 9, 14));
    return b;
  }

  public static JTextArea paragraph(String text, int size) {
    JTextArea area = new JTextArea(text);
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setOpaque(false);
    area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, size));
    area.setForeground(MUTED);
    return area;
  }

  public static void accessible(JComponent component, String name) {
    component.setName(name);
    component.getAccessibleContext().setAccessibleName(name);
  }

  public static JPanel padded(JPanel panel, int size) {
    panel.setBorder(new EmptyBorder(size, size, size, size));
    return panel;
  }
}
