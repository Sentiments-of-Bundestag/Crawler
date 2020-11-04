package xmlparser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import model.Person.Fraktion;
import model.Person.Person;
import Utils.Utils;
import model.Sitzung.Ablaufspunkt;
import org.w3c.dom.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
    static final String RELIGION_TAG = "RELIGION";
    static final String WAHLPERIODE_TAG = "WAHLPERIODE";
    static final String INSTITUTION_TAG = "INSTITUTION";
    static final String INS_ART_TAG = "INSART_LANG";
    static final String INSART_FRAKTION_TAG = "Fraktion/Gruppe";
    static final String INS_LANG_TAG = "INS_LANG";
    static final String MDBINS_VON_TAG = "MDBINS_VON";
    static final String MDBINS_BIS_TAG = "MDBINS_BIS";

    //protocol tags
    static final String REDNER_LIST_TAG = "rednerliste";
    static final String REDNER_TAG = "redner";
    static final String ATTRIBUTE_ID_TAG = "id";
    static final String SITZUNGSVERLAUF_TAG = "sitzungsverlauf";

    private File file;
    private Document doc;
    private final List<Person> personList = new ArrayList<>();

    public XMLparser(){
    }

   //TODO: add full NULL-Handling


    public List<Person> parseBaseData(String path){
        //create Doc from file
        createDoc(path);

        if(doc == null){
            System.out.println("Document wasn't created");
            return null;
        }
        //get MDB objects from xml base data
        NodeList mdbNodeList = doc.getElementsByTagName(MITGLIED_DEUTSCHER_BUNDESTAG_TAG);

        for (int i = 0; i < mdbNodeList.getLength(); i++) {
            Node mdbNode = mdbNodeList.item(i);
            if(mdbNode.getNodeType() == Node.ELEMENT_NODE){
                Element mdbElement = (Element) mdbNode;
                //get ID of persons
                int id = Integer.parseInt(getPersonID(mdbElement));
                //get properties of xml object <NAME>
                Map<String,String> personProperties = getNameProperties(mdbElement);
                //get properties of xml object <BIOGRAPHISCHE_ANGABEN>
                Map<String, String> bioDataProperties = getBioData(mdbElement);
                //get all "fraktionen" from a person
                List<Fraktion> fraktionen = getFraktionen(mdbElement);
                //create person from collected properties
                Person person = new Person(id, personProperties.get(AKAD_TITEL_TAG), personProperties.get(VORNAME_TAG), personProperties.get(NACHNAME_TAG),
                        bioDataProperties.get(BERUF_TAG), bioDataProperties.get(GESCHLECHT_TAG), Utils.StringToDate(bioDataProperties.get(GEBURTSDATUM_TAG)), bioDataProperties.get(FAMILIENSTAND_TAG), bioDataProperties.get(RELIGION_TAG), bioDataProperties.get(GEBURTSORT_TAG), fraktionen);
                personList.add(person);
            }

        }
        return personList;
    }

    public void parseProtocol(String path){
        createDoc(path);

        if(doc == null){
            System.out.println("Document wasn't created");
            return;
        }

        Element dbtplenarprotokoll = doc.getDocumentElement();

        //get rednerliste
        List<Integer> rednerIdList = getRednerList();
        List<Person> rednerList = getPersonsById(rednerIdList);

        //get sitzungsverlauf
        Node sitzungsverlaufNode = doc.getElementsByTagName(SITZUNGSVERLAUF_TAG).item(0);

        if(sitzungsverlaufNode.getNodeType() == Node.ELEMENT_NODE){
            //get Sitzungsbeginn
            Ablaufspunkt ablaufspunkt = getSitzungsbeginn((Element)sitzungsverlaufNode);
        }


        System.out.println("");

    }

    private Ablaufspunkt getSitzungsbeginn(Element sitzungsVerlaufElement){
        return null;
    }

    private List<Integer> getRednerList(){
        List<Integer> rednerIdList = new ArrayList<>();
        NodeList rednerNodeList = doc.getElementsByTagName(REDNER_LIST_TAG);

        for (int i = 0; i < rednerNodeList.getLength(); i++){
            Node rednerListNode = rednerNodeList.item(i);
            if(rednerListNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) rednerListNode;
                NodeList redner = eElement.getElementsByTagName(REDNER_TAG);
                for (int j = 0; j < redner.getLength(); j++) {
                    Node rednerNode = redner.item(j);
                    String rednerID = ((Element)rednerNode).getAttribute(ATTRIBUTE_ID_TAG);
                    rednerIdList.add(Integer.parseInt(rednerID));
                }
            }
        }
        return rednerIdList;
    }

    private List<Person> getPersonsById(List<Integer> idList){
        if(personList == null || personList.isEmpty()){
            return null;
        }
        List<Person> rednerListe = new ArrayList<>();
        for (int id :
                idList) {
            for (Person person :
                    personList) {
                if(person.getId() == id){
                    rednerListe.add(person);
                    break;
                }
            }
        }
        return rednerListe;
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
          /*  InputStream is = new FileInputStream(file);
            doc = PositionalXMLReader.readXML(is);
            is.close();*/

        }
        catch(Exception e){
            System.out.println(e.toString());
        }
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
            bioDataProperties.put(RELIGION_TAG, ((Element)bioDataNode).getElementsByTagName(RELIGION_TAG).item(0).getTextContent());
        }

        return bioDataProperties;
    }

    private List<Fraktion> getFraktionen(Element mdbElement){
        List<Fraktion> fraktionen = new ArrayList<>();

        NodeList wahlperiodeNodeList = mdbElement.getElementsByTagName(WAHLPERIODE_TAG);

        for (int i = 0; i < wahlperiodeNodeList.getLength(); i++) {
            Node wahlperiodeNode = wahlperiodeNodeList.item(i);
            if(wahlperiodeNode.getNodeType() == Node.ELEMENT_NODE){
                NodeList institutionNodeList = ((Element)wahlperiodeNode).getElementsByTagName(INSTITUTION_TAG);
                for (int j = 0; j < institutionNodeList.getLength(); j++) {
                    Node institutionNode = institutionNodeList.item(j);
                    if(institutionNode.getNodeType() == Node.ELEMENT_NODE){
                        String institutionArt = ((Element)institutionNode).getElementsByTagName(INS_ART_TAG).item(0).getTextContent();
                        if(institutionArt.equals(INSART_FRAKTION_TAG)){
                            String institutionBeschreibung = ((Element)institutionNode).getElementsByTagName(INS_LANG_TAG).item(0).getTextContent();
                            String eintritt = ((Element)institutionNode).getElementsByTagName(MDBINS_VON_TAG).item(0).getTextContent();
                            String austritt = ((Element)institutionNode).getElementsByTagName(MDBINS_BIS_TAG).item(0).getTextContent();
                            Fraktion fraktion = new Fraktion(null, institutionBeschreibung, Utils.StringToDate(eintritt), Utils.StringToDate(austritt));
                            fraktionen.add(fraktion);
                        }
                    }
                }
            }
        }
        return fraktionen;
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



}
