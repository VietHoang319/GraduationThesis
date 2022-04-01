package path_finding.dialog;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import path_finding.Map;

/**
 * 
 */
public class JFileChooserImportMap extends JFileChooserImExportData {

    public JFileChooserImportMap(Component parentComponent, Map map) {
        super(ACTION.IMPORT, parentComponent, map);
    }

    @Override
    protected void onSelectJavaDataFile(File selectedFile) {
        try {
            FileInputStream readData = new FileInputStream(selectedFile);
            ObjectInputStream readStream = new ObjectInputStream(readData);

            this.map = (Map) readStream.readObject();
            readStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map getResult() {
        return this.map;
    }
}