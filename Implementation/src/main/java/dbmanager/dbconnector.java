package dbmanager;
import com.github.underscore.lodash.Json;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.mongodb.MongoClient;
import com.mongodb.*;
import org.json.JSONObject;
import org.json.*;
import java.util.Collection;

public class dbconnector {


    public static DB  init() {

//DBCollection collection= null;
DB db=null;
        try {
            Mongo mongo = new Mongo("localhost", 27017);
             db = mongo.getDB("crawler");

          /*  String collectionName="crawlercollast";

            if (!db.getCollectionNames().contains(collectionName))
                db.createCollection( collectionName,  (DBObject) JSON.parse("null"));

             collection = db.getCollection(collectionName);*/

        } catch (Exception e) {
        }

        return db;
    }
    public static JSONObject call(BasicDBObject query, DB  db) {

        BasicDBObject all= new BasicDBObject();
        String collectionName="crawlercollast";

        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName,  (DBObject) JSON.parse("null"));

      DBCollection  collection = db.getCollection(collectionName);

       DBCursor cursor =collection.find(all,query);
       String result="";
       while (cursor.hasNext())
       {
           result+=cursor.next();
       }

        return new JSONObject(result);
    }
}


