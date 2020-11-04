import model.Person.Person;
import xmlparser.XMLparser;

import java.net.URL;
import java.util.List;

public class main {
    public static void main(String[] args) {
        XMLparser xmLparser = new XMLparser();
        List<Person> personen = xmLparser.parseBaseData("/Protokolle/Stammdaten/MDB_STAMMDATEN.XML");

        xmLparser.parseProtocol("/Protokolle/Wahlperiode/19/19184-data.xml");

    }
}
