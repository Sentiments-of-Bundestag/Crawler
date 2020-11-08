package dbmanager;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.JSONException;
import org.json.JSONObject;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.mongodb.MongoClient;
import com.mongodb.*;

public class dbloader {
    public static void dbpush( File xmlFile,DBCollection collection ) {
 try
    {
        XML xml = new XMLDocument(xmlFile);
        String xmlString = xml.toString();
        System.out.println("XML as String using JCabi library... " );
        //System.out.println(xmlString);
        String jsonString=org.json.XML.toJSONObject(xmlString).toString();
        //System.out.println(jsonString);

        DBObject obj = (DBObject) JSON.parse(jsonString);
        collection.insert(obj);

    }catch(Exception e){}
    }
}
