package path_finding;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {

    private static final long serialVersionUID = 1L;
    private final Color EVEN_COLOR = new Color(230, 240, 255);
    private final Color SELECTED_COLOR = new Color(174, 207, 255);
    private final EmptyBorder DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(3, 3, 3, 3);
    private final CompoundBorder DEFAULT_FOCUS_BORDER = new CompoundBorder(
            new LineBorder(Color.MAGENTA, 2, true), new EmptyBorder(1, 1, 1, 1));

    public TextAreaCellRenderer() {
        super();
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
                                                    boolean isSelected, boolean hasFocus, 
                                                    int row, int column) 
    {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(SELECTED_COLOR);
        } else {
            setForeground(table.getForeground());
            setBackground((row % 2 == 0) ? EVEN_COLOR : table.getBackground());
        }

        if (hasFocus)
            setBorder(isSelected ? DEFAULT_FOCUS_BORDER : DEFAULT_NO_FOCUS_BORDER);
        else
            setBorder(DEFAULT_NO_FOCUS_BORDER);

        setFont(table.getFont());
        setText((value == null) ? "" : value.toString());
        return this;
    }
}