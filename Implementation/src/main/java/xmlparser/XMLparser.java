package xmlparser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import model.Person.Fraktion;
import model.Person.Person;
import Utils.Utils;
import model.Sitzung.*;
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
    static final String WAHLPERIODE_VON_TAG = "MDBWP_VON";
    static final String WAHLPERIODE_BIS_TAG = "MDBWP_BIS";
    static final String MDBINS_VON_TAG = "MDBINS_VON";
    static final String MDBINS_BIS_TAG = "MDBINS_BIS";

    //protocol tags
    static final String REDNER_LIST_TAG = "rednerliste";
    static final String REDNER_TAG = "redner";
    static final String ATTRIBUTE_ID_TAG = "id";
    static final String SITZUNGSVERLAUF_TAG = "sitzungsverlauf";
    static final String SITZUNGSBEGINN_TAG = "sitzungsbeginn";
    static final String TAGESORDNUNGSPUNKT_TAG = "tagesordnungspunkt";
    static final String PARAGRAF_TAG = "p";
    static final String KOMMENTAR_TAG = "kommentar";
    static final String LINE_NUMBER_TAG = "lineNumber";
    static final String ATTRIBUTE_KLASSE_TAG = "klasse";

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
                var line = mdbNode.getUserData("lineNumber");
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

        //TODO: Add a new person for ids which can't be found in the base data person list but exist in the "rednerListe"
        List<Person> rednerList = getPersonsById(rednerIdList);

        //get sitzungsverlauf
        Node sitzungsverlaufNode = doc.getElementsByTagName(SITZUNGSVERLAUF_TAG).item(0);

        List<Ablaufspunkt> ablaufspunkte = new ArrayList<>();

        if(sitzungsverlaufNode.getNodeType() == Node.ELEMENT_NODE){
            //get Sitzungsbeginn
            NodeList sitzungsbeginnNodeList = ((Element)sitzungsverlaufNode).getElementsByTagName(SITZUNGSBEGINN_TAG);

            //get all redeAnteile
            List<RedeTeil> redeTeile = getAllRedeTeil(sitzungsbeginnNodeList);

            //get all reden von bis Zeile
            List<Rede> reden = getAllReden();

            //order redeAnteile zu reden

            //add zu ablaufspunkt


            NodeList tagesOrdnungsPunkteNodeList = ((Element)sitzungsverlaufNode).getElementsByTagName(TAGESORDNUNGSPUNKT_TAG);

            for (int i = 0; i < tagesOrdnungsPunkteNodeList.getLength(); i++) {
            //get everything from tagesordnungspunkt
            Ablaufspunkt ablaufspunkt = new Ablaufspunkt(AblaufspunktTyp.TAGESORDNUNGSPUNKT, "Thema", 1, null);
            ablaufspunkte.add(ablaufspunkt); }
        }


        System.out.println("");

    }

    private List<Rede> getAllReden(){
    return null;
    }

    private List<RedeTeil> getAllRedeTeil(NodeList ablaufspunktNodeList){
        List<RedeTeil> redeTeile = new ArrayList<>();
        if(ablaufspunktNodeList == null || ablaufspunktNodeList.getLength() == 0){
            return redeTeile;
        }
        for (int i = 0; i < ablaufspunktNodeList.getLength(); i++) {
            Node ablaufspunktNode = ablaufspunktNodeList.item(i);
            if(ablaufspunktNode.getNodeType() == Node.ELEMENT_NODE){
                //get all paragraphs <p>
                NodeList paragraphNodeList = ((Element)ablaufspunktNode).getElementsByTagName(PARAGRAF_TAG);

                for (int j = 0; j < paragraphNodeList.getLength(); j++) {
                    RedeTeil redeTeil = getRedeTeil(paragraphNodeList.item(j));
                    if(redeTeil != null){
                        redeTeile.add(redeTeil);
                    }
                }

                NodeList kommentarNodeList = ((Element)ablaufspunktNode).getElementsByTagName(KOMMENTAR_TAG);

                for (int k = 0; k < kommentarNodeList.getLength(); k++) {
                    RedeTeil redeTeil = getRedeTeil(kommentarNodeList.item(k));
                    if(redeTeil != null){
                        redeTeile.add(redeTeil);
                    }
                }

            }
        }
        return redeTeile;
    }

    private RedeTeil getRedeTeil(Node redeTeilNode) {
        if(redeTeilNode == null){
            return null;
        }
        String redeTeilAttribute = ((Element)redeTeilNode).getAttribute(ATTRIBUTE_KLASSE_TAG);
        if(redeTeilAttribute.equals(REDNER_TAG)){
            return null;
        }
            RedeTeilTyp redeTeilTyp;
            if(redeTeilNode.getNodeName().equals(PARAGRAF_TAG)){
                redeTeilTyp = RedeTeilTyp.PARAGRAF;
            } else if(redeTeilNode.getNodeName().equals(KOMMENTAR_TAG)){
                redeTeilTyp = RedeTeilTyp.KOMMENTAR;
            } else{
                redeTeilTyp = RedeTeilTyp.UNKNOWN;
            }
            int lineNumber = Integer.parseInt((String) redeTeilNode.getUserData(LINE_NUMBER_TAG));

            String text = redeTeilNode.getTextContent();

            return new RedeTeil(text, lineNumber, redeTeilTyp);
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
        if(personList.isEmpty()){
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

           /* DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            doc = dbBuilder.parse(file);
            doc.getDocumentElement().normalize();*/

            doc = PositionalXMLReader.readXML(file.getAbsolutePath());


        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    private String getPersonID(Element mdbElement){
        String id = "";

        NodeList idNodeList = mdbElement.getElementsByTagName(ID_TAG);
        Node idNode = idNodeList.item(0);
        var line = idNode.getUserData("lineNumber");
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
                String eintritt = ((Element)wahlperiodeNode).getElementsByTagName(WAHLPERIODE_VON_TAG).item(0).getTextContent();
                String austritt = ((Element)wahlperiodeNode).getElementsByTagName(WAHLPERIODE_BIS_TAG).item(0).getTextContent();
                NodeList institutionNodeList = ((Element)wahlperiodeNode).getElementsByTagName(INSTITUTION_TAG);
                for (int j = 0; j < institutionNodeList.getLength(); j++) {
                    Node institutionNode = institutionNodeList.item(j);
                    if(institutionNode.getNodeType() == Node.ELEMENT_NODE){
                        String institutionArt = ((Element)institutionNode).getElementsByTagName(INS_ART_TAG).item(0).getTextContent();
                        if(institutionArt.equals(INSART_FRAKTION_TAG)){
                            String institutionBeschreibung = ((Element)institutionNode).getElementsByTagName(INS_LANG_TAG).item(0).getTextContent();
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
