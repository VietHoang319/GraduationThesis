package path_finding.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JDialogCustomMapSize implements ChangeListener {
    // default
    public static final int DEF_COLUMNS = 20;
    public static final int DEF_ROWS = 20;

    static final String NxN_Size = "n x n";
    static final String MxN_Size = "m x n";

    // JSlider default params value
    static final int DEF_MAJOR = 5;
    static final int DEF_MINOR = 10;
    static final int DEF_MIN_VAL = 5;
    static final int DEF_MAX_VAL = 50;

    CardLayout cl = new CardLayout();
    JPanel panel = new JPanel(new BorderLayout(0, 14));
    JPanel pCard = new JPanel(cl);
    JSlider slNxN = new JSlider(DEF_MIN_VAL, DEF_MAX_VAL, DEF_COLUMNS);
    JSlider slColumns = new JSlider(DEF_MIN_VAL, DEF_MAX_VAL, DEF_COLUMNS);
    JSlider slRows = new JSlider(DEF_MIN_VAL, DEF_MAX_VAL, DEF_ROWS);
    SpinnerModel nXnModel = new SpinnerNumberModel(DEF_COLUMNS, DEF_MIN_VAL, DEF_MAX_VAL, 1);
    SpinnerModel columnsModel = new SpinnerNumberModel(DEF_COLUMNS, DEF_MIN_VAL, DEF_MAX_VAL, 1);
    SpinnerModel rowsModel = new SpinnerNumberModel(DEF_ROWS, DEF_MIN_VAL, DEF_MAX_VAL, 1);
    JSpinner spNxN = new JSpinner(nXnModel);
    JSpinner spColumns = new JSpinner(columnsModel);
    JSpinner spRows = new JSpinner(rowsModel);
    JLabel lbCurrentSize = new JLabel(getMapSize());

    int columns = DEF_COLUMNS;
    int rows = DEF_ROWS;

    public JDialogCustomMapSize() {
        JPanel pComboBox = new JPanel();
        String[] comboBoxItem = { NxN_Size, MxN_Size };
        JComboBox<String> cbCard = new JComboBox<>(comboBoxItem);

        cbCard.setEditable(false);
        cbCard.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cl.show(pCard, (String) e.getItem());
            }
        });
        pComboBox.add(new JLabel("Choose map type:  "), BorderLayout.PAGE_START);
        pComboBox.add(cbCard);
        panel.add(pComboBox, BorderLayout.PAGE_START);

        JPanel pNxN = new JPanel();
        ((JSpinner.DefaultEditor) spNxN.getEditor()).getTextField().setColumns(4);
        pNxN.add(getSliderPanel(getJSlider(slNxN, spNxN), spNxN, "Number of columns and rows (n)"));

        JPanel pMxN = new JPanel();
        BoxLayout boxLayout = new BoxLayout(pMxN, BoxLayout.Y_AXIS);
        pMxN.setLayout(boxLayout);
        ((JSpinner.DefaultEditor) spColumns.getEditor()).getTextField().setColumns(4);
        ((JSpinner.DefaultEditor) spRows.getEditor()).getTextField().setColumns(4);
        pMxN.add(getSliderPanel(getJSlider(slColumns, spColumns), spColumns, "Number of columns (m)"));
        pMxN.add(getSliderPanel(getJSlider(slRows, spRows), spRows, "Number of rows (n)"));

        pCard.add(pNxN, NxN_Size);
        pCard.add(pMxN, MxN_Size);
        panel.add(pCard, BorderLayout.CENTER);

        lbCurrentSize.setOpaque(true);
        lbCurrentSize.setBackground(Color.MAGENTA);
        lbCurrentSize.setForeground(Color.WHITE);
        lbCurrentSize.setPreferredSize(new Dimension(100, 25));
        lbCurrentSize.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lbCurrentSize, BorderLayout.PAGE_END);
    }

    public int show() {
        return JOptionPane.showConfirmDialog(
            null, 
            panel, 
            "Custom map size", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == slNxN) {
            columns = slNxN.getValue();
            rows = slNxN.getValue();
        } else if (e.getSource() == slColumns) {
            columns = slColumns.getValue();
        } else if (e.getSource() == slRows) {
            rows = slRows.getValue();
        }
        lbCurrentSize.setText(getMapSize());
    }

    private JSlider getJSlider(JSlider slider, JSpinner spinner) {
        slider.setMajorTickSpacing(DEF_MAJOR);
        slider.setMinorTickSpacing(DEF_MINOR);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.addChangeListener(this);
        slider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                spinner.setValue(slider.getValue());
            }
        });
        spinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                slider.setValue((int) spinner.getValue());
            }
        });

        return slider;
    }

    private JPanel getSliderPanel(JSlider slider, JSpinner spinner, String description) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(new JLabel(description), BorderLayout.LINE_START);
        panel.add(new JLabel("        "), BorderLayout.CENTER);
        panel.add(spinner, BorderLayout.LINE_END);
        panel.add(slider, BorderLayout.PAGE_END);

        return panel;
    }

    private String getMapSize() {
        return "Map size is " + columns + " x " + rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }
}
