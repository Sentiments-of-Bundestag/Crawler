import xmlparser.XMLparser;

import java.net.URL;

public class main {
    public static void main(String[] args) {
        XMLparser xmLparser = new XMLparser();
        xmLparser.parseBaseData("/Protokolle/Stammdaten/MDB_STAMMDATEN.XML");
    }
}
