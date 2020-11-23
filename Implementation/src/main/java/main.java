<<<<<<< HEAD
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.json.JSONObject;
=======
import models.Person.Person;
>>>>>>> main
import xmlparser.XMLparser;
import dbmanager.dbconnector;
import dbmanager.dbloader;
import com.mongodb.*;

import java.io.File;
import java.net.URL;
<<<<<<< HEAD
import java.nio.file.FileSystems;
=======
import java.util.List;
>>>>>>> main

public class main {
    public static void main(String[] args) {
        XMLparser xmLparser = new XMLparser();
<<<<<<< HEAD
        xmLparser.parseBaseData("/Protokolle/Stammdaten/MDB_STAMMDATEN.XML");
        TestDB( "/Protokolle/Wahlperiode/19/19184-data.xml");
    }
     static void  TestDB(String input){
        File file = new File(new File("").getAbsolutePath() + input);
        DB  db=dbconnector.init();

        dbloader.dbpush(file, db);

        //Mongo DB Query
        BasicDBObject query= new BasicDBObject();
        query.put("name","Müller");
        JSONObject json= dbconnector.call(query,db );
        //System.out.println(json.toString());
=======
        List<Person> personen = xmLparser.parseBaseData("/Protokolle/Stammdaten/MDB_STAMMDATEN.XML");

        xmLparser.parseProtocol("/Protokolle/Wahlperiode/19/19184-data.xml");

>>>>>>> main
    }
}
