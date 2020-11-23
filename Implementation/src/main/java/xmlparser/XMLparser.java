package xmlparser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import models.Person.Person;
import models.Person.Redner;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLparser {
    //base data tags
    static final String MITGLIED_DEUTSCHER_BUNDESTAG_TAG = "MDB";
    static final String NAME_TAG = "NAME";
    static final String VORNAME_TAG = "VORNAME";
    static final String NACHNAME_TAG = "NACHNAME";
    static final String AKAD_TITEL_TAG = "AKAD_TITEL";
    static final String ID_TAG = "ID";
    static final String BIO_DATA_TAG = "BIOGRAFISCHE_ANGABEN";
    static final String GEBURTSDATUM_TAG = "GEBURTSDATUM";
    static final String GEBURTSORT_TAG = "GEBURTSORT";
    static final String GESCHLECHT_TAG = "GESCHLECHT";
    static final String FAMILIENSTAND_TAG = "FAMILIENSTAND";
    static final String BERUF_TAG = "BERUF";
    static final String RELIGIO_TAG = "RELIGION";

    private File file;
    private Document doc;

    public XMLparser(){
    }

    private String getPersonID(Element mdbElement){
        String id = "";

        NodeList idNodeList = mdbElement.getElementsByTagName(ID_TAG);
        Node idNode = idNodeList.item(0);
        id = idNode.getTextContent();

        return id;
    }
    private Map<String, String> getBioData(Element mdbElement){
        Map<String, String> bioDataProperties = new HashMap<>();

        NodeList bioDataNodeList = mdbElement.getElementsByTagName(BIO_DATA_TAG);
        Node bioDataNode = bioDataNodeList.item(0);

        if(bioDataNode.getNodeType() == Node.ELEMENT_NODE){
            bioDataProperties.put(GEBURTSDATUM_TAG, ((Element)bioDataNode).getElementsByTagName(GEBURTSDATUM_TAG).item(0).getTextContent());
            bioDataProperties.put(GEBURTSORT_TAG, ((Element)bioDataNode).getElementsByTagName(GEBURTSORT_TAG).item(0).getTextContent());
            bioDataProperties.put(GESCHLECHT_TAG, ((Element)bioDataNode).getElementsByTagName(GESCHLECHT_TAG).item(0).getTextContent());
            bioDataProperties.put(FAMILIENSTAND_TAG, ((Element)bioDataNode).getElementsByTagName(FAMILIENSTAND_TAG).item(0).getTextContent());
            bioDataProperties.put(BERUF_TAG, ((Element)bioDataNode).getElementsByTagName(BERUF_TAG).item(0).getTextContent());
            bioDataProperties.put(RELIGIO_TAG, ((Element)bioDataNode).getElementsByTagName(RELIGIO_TAG).item(0).getTextContent());
        }

        return bioDataProperties;
    }

    private Map<String, String> getNameProperties(Element mdbElement){
        Map<String, String> nameProperties = new HashMap<>();

        NodeList nameNodeList = mdbElement.getElementsByTagName(NAME_TAG);

        for (int i = 0; i < nameNodeList.getLength(); i++) {
            Node nameNode = nameNodeList.item(i);
            if(nameNode.getNodeType() == Node.ELEMENT_NODE){
                nameProperties.put(NACHNAME_TAG, ((Element)nameNode).getElementsByTagName(NACHNAME_TAG).item(0).getTextContent());
                nameProperties.put(VORNAME_TAG, ((Element)nameNode).getElementsByTagName(VORNAME_TAG).item(0).getTextContent());
                nameProperties.put(AKAD_TITEL_TAG, ((Element)nameNode).getElementsByTagName(AKAD_TITEL_TAG).item(0).getTextContent());
            }
        }
        return nameProperties;
    }

    public void parseBaseData(String path){
        createDoc(path);

        if(doc == null){
            System.out.println("Document wasn't created");
            return;
        }
        NodeList mdbNodeList = doc.getElementsByTagName(MITGLIED_DEUTSCHER_BUNDESTAG_TAG);

        List<Person> personen = new ArrayList<>();
        List<Redner> rednerList = new ArrayList<>();

        for (int i = 0; i < mdbNodeList.getLength(); i++) {
            Node mdbNode = mdbNodeList.item(i);
            if(mdbNode.getNodeType() == Node.ELEMENT_NODE){
                Element mdbElement = (Element) mdbNode;
                int id = Integer.parseInt(getPersonID(mdbElement));
                Map<String,String> personProperties = getNameProperties(mdbElement);
                Map<String, String> bioDataProperties = getBioData(mdbElement);
                Person person = new Person(personProperties.get(AKAD_TITEL_TAG), personProperties.get(VORNAME_TAG), personProperties.get(NACHNAME_TAG),
                        bioDataProperties.get(BERUF_TAG), bioDataProperties.get(GESCHLECHT_TAG), bioDataProperties.get(GEBURTSDATUM_TAG), bioDataProperties.get(FAMILIENSTAND_TAG), bioDataProperties.get(RELIGIO_TAG), bioDataProperties.get(GEBURTSORT_TAG), null);
                personen.add(person);
                rednerList.add(new Redner(id, person));
            }

        }
        System.out.println("");
    }

    public void parseProtocol(String path){
        createDoc(path);

        if(doc == null){
            System.out.println("Document wasn't created");
            return;
        }

        Element dbtplenarprotokoll = doc.getDocumentElement();
        NodeList rednerList = doc.getElementsByTagName("rednerliste");

        for (int i = 0; i < rednerList.getLength(); i++){
            Node rednerListNode = rednerList.item(i);
            if(rednerListNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) rednerListNode;
                NodeList redner = eElement.getElementsByTagName("redner");
                for (int j = 0; j < redner.getLength(); j++) {
                    Node rednerNode = redner.item(j);
                    String rednerID = ((Element)rednerNode).getAttribute("id");

                }
            }
        }
    }

    private void createDoc(String path){
        if(path == null || path.equals("")){
            System.out.println("No file name was given");
            return;
        }
        try{
            file = new File(new File("").getAbsolutePath() + path);
            System.out.println(file.getAbsolutePath());

            if(!file.exists()){
                System.out.println("File doesn't exist");
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            doc = dbBuilder.parse(file);
            doc.getDocumentElement().normalize();

        }
        catch(Exception e){
            System.out.println(e);
        }
    }



}
