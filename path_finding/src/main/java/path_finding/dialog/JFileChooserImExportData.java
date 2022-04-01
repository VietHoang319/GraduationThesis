package path_finding.dialog;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import path_finding.Map;

/**
 * 
 */
abstract public class JFileChooserImExportData {
    static enum ACTION {
        IMPORT, EXPORT
    };

    JFileChooser fileChooser;
    FileNameExtensionFilter javaMapFileFilter = new FileNameExtensionFilter("Java Data File (*.jmap)", "jmap");

    Component parentComponent;

    Map map;
    int response = JFileChooser.ERROR_OPTION;

    private ACTION action;

    public JFileChooserImExportData(ACTION action, Component parentComponent, Map map) {
        this.parentComponent = parentComponent;
        this.map = map;
        this.action = action;

        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(action == ACTION.IMPORT ? "Import file" : "Export file");
        if (action == ACTION.EXPORT)
            fileChooser.setSelectedFile(new File("Untitled.jmap"));
        fileChooser.addChoosableFileFilter(javaMapFileFilter);
        fileChooser.setFileFilter(javaMapFileFilter);
    }

    public void show() {
        if (action == ACTION.EXPORT)
            response = fileChooser.showSaveDialog(parentComponent);
        else
            response = fileChooser.showOpenDialog(parentComponent);

        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (action == ACTION.EXPORT) {
                String fileName = selectedFile.getName();
                String path;
                int indexOfExtension = fileName.lastIndexOf('.');
                if (indexOfExtension == -1) {
                    path = fileChooser.getSelectedFile().getAbsolutePath() 
                        + "." 
                        + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
                } else {
                    String extension = fileName.substring(indexOfExtension, fileName.length());
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!extension.equals(".jmap"))
                        path += "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
                }
                
                selectedFile = new File(path);
            }

            if (selectedFile == null) return;

            if (action == ACTION.EXPORT && selectedFile.exists()) {
                int result = JOptionPane.showConfirmDialog(
                        parentComponent, // nếu để là fileChooser -> ko hiển thị giữa form chính
                        "File đã tồn tại, ghi đè?", 
                        "File đã tồn tại",
                        JOptionPane.OK_CANCEL_OPTION
                );
                switch (result) {
                    case JOptionPane.CLOSED_OPTION:
                    case JOptionPane.CANCEL_OPTION:
                        return;
                    // case JOptionPane.OK_OPTION: // do nothing
                }
            }

            if (fileChooser.getFileFilter() == javaMapFileFilter) {
                onSelectJavaDataFile(selectedFile);
            }
        }
    }

    abstract protected void onSelectJavaDataFile(File selectedFile);

    public int getResponse() {
        return response;
    }
}