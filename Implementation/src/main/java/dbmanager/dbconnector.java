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
    public static DBCollection init() {

DBCollection collection= null;

        try {
            Mongo mongo = new Mongo("localhost", 27017);
            DB db = mongo.getDB("crawler");

            String collectionName="crawlercollast";

            if (!db.getCollectionNames().contains(collectionName))
                db.createCollection( collectionName,  (DBObject) JSON.parse("null"));

             collection = db.getCollection(collectionName);

        } catch (Exception e) {
        }

        return collection;
    }
    public static JSONObject call(BasicDBObject query, DBCollection collection) {

        BasicDBObject all= new BasicDBObject();

       DBCursor cursor =collection.find(all,query);
       String result="";
       while (cursor.hasNext())
       {
           result+=cursor.next();
       }

        return new JSONObject(result);
    }
}


