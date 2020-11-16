import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.json.JSONObject;
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
        TestDB( "/Protokolle/Wahlperiode/19/19184-data.xml");
    }
     static void  TestDB(String input){
        File file = new File(new File("").getAbsolutePath() + input);
        DBCollection DBC=dbconnector.init();
        dbloader.dbpush(file, DBC);

        //Mongo DB Query
        BasicDBObject query= new BasicDBObject();
        query.put("name","Müller");
        JSONObject json= dbconnector.call(query,DBC );
        //System.out.println(json.toString());
    }
}
