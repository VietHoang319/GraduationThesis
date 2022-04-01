package path_finding.dialog;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import path_finding.Map;

/**
 * 
 */
public class JFileChooserExportMap extends JFileChooserImExportData {

    public JFileChooserExportMap(Component parentComponent, Map map) {
        super(ACTION.EXPORT, parentComponent, map);
    }

    // https://samderlust.com/dev-blog/java/write-read-arraylist-object-file-java#:~:text=Write%20an%20object%20into%20a%20file.&text=ObjectOutputStream%20writeStream%20%3D%20new%20ObjectOutputStream(writeData)%3A%20ObjectOutputStream%20will%20handle%20the,an%20object%20to%20a%20file.
    @Override
    protected void onSelectJavaDataFile(File selectedFile) {
        try {
            FileOutputStream writeData = new FileOutputStream(selectedFile);
            ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

            writeStream.writeObject(this.map);
            writeStream.flush();
            writeStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}