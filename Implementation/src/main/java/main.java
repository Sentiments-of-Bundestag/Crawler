import xmlparser.XMLparser;
import dbmanager.dbconnector;
import dbmanager.dbloader;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;

public class main {
    public static void main(String[] args) {
        XMLparser xmLparser = new XMLparser();
        xmLparser.parseBaseData("/Protokolle/Stammdaten/MDB_STAMMDATEN.XML");

        String path= "/Protokolle/Wahlperiode/19/19184-data.xml";
        File file = new File(new File("").getAbsolutePath() + path);
        dbloader.dbpush(file, dbconnector.init());
    }
}
