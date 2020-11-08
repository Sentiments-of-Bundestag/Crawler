package dbmanager;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.mongodb.MongoClient;
import com.mongodb.*;

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
}


