package path_finding;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PathTable extends JTable {
    private static final long serialVersionUID = 1L;

    String[] columnNames = { "key", "data" };
    HashMap<String, ArrayList<ArrayList<Node>>> data = new HashMap<>();
    DefaultTableModel model = new DefaultTableModel(new Object[][] {}, columnNames);

    ArrayList<Node> selectedPath;

    public PathTable() {
        setShowGrid(false);
        setFocusable(true);
        setTableHeader(null);
        setModel(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = getSelectedRow();
                if (row == -1) {
                    selectedPath = null;
                    return;
                }
                
                String combinedKkey = model.getValueAt(row, 0).toString();
                if (combinedKkey.length() == 0)
                    selectedPath = null;
                else {
                    String[] split = combinedKkey.split("@", 2);
                    selectedPath = data.get(split[0]).get(Integer.valueOf(split[1]));
                }
            }
        });
        getColumn("key").setMinWidth(0);
        getColumn("key").setMaxWidth(0);
        getColumn("key").setWidth(0);
        getColumnModel().getColumn(1).setCellRenderer(new TextAreaCellRenderer());
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * @param title       allow empty string or null
     * @param nameForData the name must be unique for all over data. NOT contain
     *                    {@code @} symbol
     */
    public void addData(String title, String nameForData, ArrayList<ArrayList<Node>> data, String[] dataInString) {
        Object[] rowData;
        ArrayList<ArrayList<Node>> clone = new ArrayList<>(data);

        if (title != null && title.length() != 0) {
            rowData = new Object[2];
            rowData[0] = "";
            rowData[1] = title;
            model.addRow(rowData);
        }

        for (int i = 0; i < dataInString.length; i++) {
            rowData = new Object[2];
            rowData[0] = nameForData + '@' + i;
            rowData[1] = dataInString[i];
            model.addRow(rowData);
        }

        this.data.put(nameForData, clone);
    }

    public void addTitle(String title) {
        Object[] rowData;
        rowData = new Object[2];
        rowData[0] = "";
        rowData[1] = title;
        model.addRow(rowData);
    }

    public void clearData() {
        data.clear();
        model.setRowCount(0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    @Override
    public void doLayout() {
        TableColumn col = getColumnModel().getColumn(1);
        for (int row = 0; row < getRowCount(); row++) {
            Component c = prepareRenderer(col.getCellRenderer(), row, 1);
            if (c instanceof JTextArea) {
                JTextArea a = (JTextArea) c;
                int h = a.getPreferredSize().height;
                if (getRowHeight(row) < h) {
                    setRowHeight(row, h);
                }
            }
        }
        super.doLayout();
    }

    public ArrayList<Node> getSelectedPath() {
        return selectedPath;
    }
}
