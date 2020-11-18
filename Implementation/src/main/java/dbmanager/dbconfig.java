package dbmanager;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import models.Person.*;
import models.Sitzung.*;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
 import com.mongodb.*;
import dbmanager.dbconnector;
public class dbconfig {
    DB db;
    public dbconfig(){
          db=dbconnector.init();
    }

   void add_fraktion(Fraktion fraktion){

       String collectionName="fraktion";
       if (!db.getCollectionNames().contains(collectionName))
           db.createCollection( collectionName,   new BasicDBObject());

      DBCollection fraktion_col = db.getCollection(collectionName);

      String json = "{ 'id' : "+fraktion_col.count()+" , " +
              "'name' : "+fraktion.getName()+" , " +
               "'beschreibung' : "+fraktion.getBeschreibung()+" , " +
               "'eintrittsDatum' :"+fraktion.getEintrittsDatum()+  "," +
               "'austrittsDatum' :  "+fraktion.getAustrittsDatum()+
       "}";
      fraktion_col.insert((DBObject)JSON.parse(json));

    }
    void add_person(Person person){
        String collectionName="person";
        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName, new BasicDBObject());

        DBCollection person_col = db.getCollection(collectionName);
        String json = "{ 'id' : "+person_col.count()+" , " +
                "'titel' : "+person.getTitel()+" , " +
                "'vorname' : "+person.getVorname()+" , " +
                "'nachname' :"+person.getNachname()+  "," +
                "'beruf' :"+person.getBeruf()+  "," +
                "'geschlecht' :"+person.getGeschlecht()+  "," +
                "'religion' :"+person.getReligion()+  "," +
                "'familienstand' :"+person.getFamilienstand()+  "," +
                "'geburtsdatum' :"+person.getGeburtsdatum()+  "," +
                "'geburtsort' :"+person.getGeburtsort()+  "," +
                "}";
        person_col.insert((DBObject)JSON.parse(json));
    }
    void add_redner(Redner redner){
        String collectionName="redner";
        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName,  new BasicDBObject());

        DBCollection redner_col = db.getCollection(collectionName);
        String json = "{ 'protokol_id' : "+redner.getId()+" , " +
                "'redner' : "+redner.getPerson()+" , " +
                "}";
        redner_col.insert((DBObject)JSON.parse(json));
    }
    void add_ablaufpunkt(Ablaufspunkt ablaufspunkt){
        String collectionName="ablaufspunkt";
        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName, new BasicDBObject());

        DBCollection ablaufspunkt_col = db.getCollection(collectionName);
        String json = "{ 'id' : "+ablaufspunkt_col.count()+" , " +
                "'ablauf_typ' : "+ablaufspunkt.getAblaufTyp()+" , " +
                "'thema' : "+ablaufspunkt.getThema()+" , " +
                "'zeile_nr' :"+ablaufspunkt.getZeile_nr()+  "," +
                "'sitzungsverlauf_protokoll_id' :"+   "," +
                "}";
        ablaufspunkt_col.insert((DBObject)JSON.parse(json));
    }
    void add_rede(Rede rede){
        String collectionName="rede";
        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName, new BasicDBObject());

        DBCollection rede_col = db.getCollection(collectionName);

    }
    void add_redeteil(RedeTeil redeteil){
        String collectionName="redeteil";
        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName, new BasicDBObject());

        DBCollection redeteil_col = db.getCollection(collectionName);
    }
    void add_sitzungsverlauf(Sitzungsverlauf sitzungsverlauf){
        String collectionName="sitzungsverlauf";
        if (!db.getCollectionNames().contains(collectionName))
            db.createCollection( collectionName, new BasicDBObject());

        DBCollection sitzungsverlauf_col = db.getCollection(collectionName);
    }

}
