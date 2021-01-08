package crawler.core.assets.impl.xmlparser;

import crawler.utils.ParseUtilities;
import models.Person.Fraktion;
import models.Person.Person;
import models.Protokoll;
import models.Sitzung.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    static final String NAME_REDE_TAG = "name";
    static final String SMALL_ID_TAG = "id";
    static final String PARAGRAF_N_TAG = "N";
    static final String REDE_TAG = "rede";
    static final String TOP_ID_TAG = "top-id";
    static final String SITZUNG_NR_TAG = "sitzung-nr";
    static final String SITZUNG_START_TIME_TAG = "sitzung-start-uhrzeit";
    static final String SITZUNG_ENDE_TIME_TAG = "sitzung-ende-uhrzeit";
    static final String SITZUNG_ORT_TAG = "sitzung-ort";
    static final String SITZUNG_NAECHSTE_DATUM = "sitzung-naechste-datum";
    static final String SITZUNG_ISSN_TAG =  "issn";
    static final String SITZUNG_DATUM_TAG = "sitzung-datum";
    static final String WAHLPERIODE_PROTOCOL_TAG = "wahlperiode";
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLparser.class);
    private static final String DRUCKSACHE_TAG = "T_Drs";
    private static final String T_FETT_TAG = "T_fett";
    private static final String T_NAS_TAG = "T_NaS";

    private File file;
    private Document doc;
    private Set<Person> personBaseData = new LinkedHashSet<>();
    private Set<Person> rednerList = new LinkedHashSet<>();

    public XMLparser() {
    }

    public XMLparser(Set<Person> personBaseData) {
        this.personBaseData = personBaseData;
    }

    //TODO: add full NULL-Handling


    public Set<Person> parseBaseData(String path) {
        //create Doc from file
        createDoc(path);

        if (doc == null) {
            LOGGER.error("Document wasn't created");
            return null;
        }
        //get MDB objects from xml base data
        NodeList mdbNodeList = doc.getElementsByTagName(MITGLIED_DEUTSCHER_BUNDESTAG_TAG);

        for (int i = 0; i < mdbNodeList.getLength(); i++) {
            Node mdbNode = mdbNodeList.item(i);
            if (mdbNode.getNodeType() == Node.ELEMENT_NODE) {
                Element mdbElement = (Element) mdbNode;
                //get ID of persons
                int id = Integer.parseInt(getPersonID(mdbElement));
                //get properties of xml object <NAME>
                Map<String, String> personProperties = getNameProperties(mdbElement);
                //get properties of xml object <BIOGRAPHISCHE_ANGABEN>
                Map<String, String> bioDataProperties = getBioData(mdbElement);
                //get all "fraktionen" from a person
                List<Fraktion> fraktionen = getFraktionen(mdbElement);
                //create person from collected properties
                Person person = new Person(id, personProperties.get(AKAD_TITEL_TAG), personProperties.get(VORNAME_TAG), personProperties.get(NACHNAME_TAG),
                        bioDataProperties.get(BERUF_TAG), bioDataProperties.get(GESCHLECHT_TAG), ParseUtilities.stringToDate(bioDataProperties.get(GEBURTSDATUM_TAG)), bioDataProperties.get(FAMILIENSTAND_TAG), bioDataProperties.get(RELIGION_TAG), bioDataProperties.get(GEBURTSORT_TAG), fraktionen);
                personBaseData.add(person);
            }

        }
        return personBaseData;
    }

    public Protokoll parseProtocol(String path) {
        createDoc(path);

        if (doc == null) {
            LOGGER.error("Document wasn't created");
            return null;
        }

        Element dbtplenarprotokoll = doc.getDocumentElement();

        //get rednerliste
        List<Integer> rednerIdList = getRednerIDList();

        //TODO: Add a new person for ids which can't be found in the base data person list but exist in the "rednerListe"

        for (int id : rednerIdList) {
            Person person = getPersonById(id);
            if (person != null) {
                rednerList.add(person);
            }
        }

        //get sitzungsverlauf
        Node sitzungsverlaufNode = doc.getElementsByTagName(SITZUNGSVERLAUF_TAG).item(0);

        List<Ablaufspunkt> ablaufspunkte = new ArrayList<>();

        if (sitzungsverlaufNode.getNodeType() == Node.ELEMENT_NODE) {
            //get Sitzungsbeginn
            NodeList sitzungsbeginnNodeList = ((Element) sitzungsverlaufNode).getElementsByTagName(SITZUNGSBEGINN_TAG);

            for (int i = 0; i < sitzungsbeginnNodeList.getLength(); i++) {
                Node sitzungsbeginnNode = sitzungsbeginnNodeList.item(i);
                ablaufspunkte.add(getAblaufspunkt(sitzungsbeginnNode));
            }

            NodeList tagesOrdnungsPunkteNodeList = ((Element) sitzungsverlaufNode).getElementsByTagName(TAGESORDNUNGSPUNKT_TAG);

            for (int i = 0; i < tagesOrdnungsPunkteNodeList.getLength(); i++) {
                Node tagesNode = tagesOrdnungsPunkteNodeList.item(i);
                ablaufspunkte.add(getAblaufspunkt(tagesNode));
            }
        }

        //get sitzungNr
        String sitzungID = dbtplenarprotokoll.getAttribute(SITZUNG_NR_TAG);

        //get sitzung start time
        Date sitzungStart = ParseUtilities.stringToDate(dbtplenarprotokoll.getAttribute(SITZUNG_START_TIME_TAG));

        //get sitzung end time
        Date sitzungEnd = ParseUtilities.stringToDate(dbtplenarprotokoll.getAttribute(SITZUNG_ENDE_TIME_TAG));

        Sitzungsverlauf sitzung = new Sitzungsverlauf(Integer.parseInt(sitzungID), sitzungStart, sitzungEnd, ablaufspunkte);

        //get ort
        String ort = dbtplenarprotokoll.getAttribute(SITZUNG_ORT_TAG);

        //get nächste Sitzung Datum
        Date naechsteSitzung = ParseUtilities.stringToDate(dbtplenarprotokoll.getAttribute(SITZUNG_NAECHSTE_DATUM));

        //get date of sitzung
        Date sitzungDatum = ParseUtilities.stringToDate(dbtplenarprotokoll.getAttribute(SITZUNG_DATUM_TAG));

        //get issn
        String issn = dbtplenarprotokoll.getAttribute(SITZUNG_ISSN_TAG);

        //get wahlperiode
        String wahlperiode = dbtplenarprotokoll.getAttribute(WAHLPERIODE_PROTOCOL_TAG);

        return new Protokoll(Integer.parseInt((wahlperiode+sitzungID)), ort, naechsteSitzung, sitzungDatum, issn, rednerList, sitzung);
    }

    private Ablaufspunkt getAblaufspunkt(Node node) {

        //get all redeAnteile
        List<RedeTeil> redeTeile = getAllRedeTeil(node);

        HashMap<Integer, String> rednerStartpoints = getRedner(node);

        HashMap<Integer, String> redenIdLine = getReden(node);

        List<Rede> reden = assignReden(redeTeile, rednerStartpoints, redenIdLine);

        int lineNumber = Integer.parseInt((String) node.getUserData(LINE_NUMBER_TAG));

        if (node.getNodeType() == Node.ELEMENT_NODE) {

            return new Ablaufspunkt(getAblaufspunktTyp(node), ((Element) node).getAttribute(TOP_ID_TAG), lineNumber, reden);
        } else {
            return new Ablaufspunkt(getAblaufspunktTyp(node), null, lineNumber, reden);
        }

    }

    private HashMap<Integer, String> getReden(Node node) {
        HashMap<Integer, String> result = new HashMap<>();
        if (node == null) {
            LOGGER.error("getReden: node is null!");
            return result;
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            //get <reden>
            NodeList redeNodeList = ((Element) node).getElementsByTagName(REDE_TAG);
            for (int j = 0; j < redeNodeList.getLength(); j++) {
                Node redeNode = redeNodeList.item(j);
                int lineNumber = -1;
                if (redeNode.getUserData(LINE_NUMBER_TAG) instanceof String) {
                    lineNumber = Integer.parseInt((String) redeNode.getUserData(LINE_NUMBER_TAG));
                }
                if (redeNode.getNodeType() == Node.ELEMENT_NODE) {
                    String id = ((Element) redeNode).getAttribute(SMALL_ID_TAG);
                    result.put(lineNumber, id);
                }
            }
        }
        return result;
    }

    private List<Rede> assignReden(List<RedeTeil> redeTeile, HashMap<Integer, String> rednerStartpoints, HashMap<Integer, String> redenIdLine) {
        Integer[] arrStartPoints = ParseUtilities.sort(rednerStartpoints.keySet().toArray(new Integer[0]), 0, rednerStartpoints.size() - 1);
        List<Rede> reden = new ArrayList<>();

        for (int i = 0; i < arrStartPoints.length; i++) {
            List<RedeTeil> parts = new ArrayList<>();
            List<RedeTeil> partsToRemove = new ArrayList<>();
            for (RedeTeil redeTeil : redeTeile) {
                int lineNumber = redeTeil.getZeileNr();
                if (i == arrStartPoints.length - 1) {
                    parts.add(redeTeil);
                    partsToRemove.add(redeTeil);
                    continue;
                }
                if (arrStartPoints[i] < lineNumber && arrStartPoints[i + 1] > lineNumber) {
                    parts.add(redeTeil);
                    partsToRemove.add(redeTeil);
                }
            }
            redeTeile.removeAll(partsToRemove);

            //get redner name or id
            String rednerString = rednerStartpoints.get(arrStartPoints[i]);

            //check if it is a name or an id
            long id;
            try {
                id = Integer.parseInt(rednerString);
            } catch (NumberFormatException e) {
                id = resolvePersonData(rednerString);
                isContainingRedner(id);
                if (id == -1) {
                    LOGGER.error("Person with the data: " + rednerString + " couldn't be resolved");
                }
            }

            //check if rede id exists
            reden.add(new Rede(redenIdLine.getOrDefault(arrStartPoints[i] - 1, null), arrStartPoints[i], parts, id));

        }
        return reden;
    }

    private long resolvePersonData(String personData) {
        String[] personDataSplit = personData.trim().toLowerCase().split("[\\s.]+");
        int highestEqualityScore = 0;
        long id = -1;

        for (int i = 0; i < personDataSplit.length; i++) {
            personDataSplit[i] = personDataSplit[i].replaceAll("[^a-zA-Z\\äöü\\-]", "");
        }

        for (Person person : personBaseData) {
            int equalityScore = 0;
            String personString = (person.getVorname() + " " + person.getNachname() + " " + person.getBeruf() + " " + person.getTitel()).toLowerCase();

            for (String part : personDataSplit) {
                if (personString.contains(part)) {
                    equalityScore++;
                }
            }
            if (highestEqualityScore < equalityScore) {
                highestEqualityScore = equalityScore;
                id = person.getId();
            }
        }
        return id;
    }

    private void isContainingRedner(long id){
        if(id != -1){
            final long finalId = id;
            Optional<Person> result = rednerList.stream().filter(person -> person.getId() == finalId).findAny();
            if(result.isEmpty()){
                Person person = getPersonById(finalId);
                if(person != null){
                    rednerList.add(person);
                }
            }
        }
    }

    private AblaufspunktTyp getAblaufspunktTyp(Node node) {
        switch (node.getNodeName()) {
            case TAGESORDNUNGSPUNKT_TAG:
                return AblaufspunktTyp.TAGESORDNUNGSPUNKT;

            case SITZUNGSBEGINN_TAG:
                return AblaufspunktTyp.SITZUNGSBEGINN;

            default:
                return AblaufspunktTyp.UNKNOWN;
        }
    }

    private HashMap<Integer, String> getRedner(Node node) {
        HashMap<Integer, String> result = new HashMap<>();
        if (node == null) {
            LOGGER.error("getRedner: node is null!");
            return result;
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            //get <name>
            NodeList nameNodeList = ((Element) node).getElementsByTagName(NAME_REDE_TAG);
            for (int j = 0; j < nameNodeList.getLength(); j++) {
                Node nameNode = nameNodeList.item(j);
                Node parentNode = nameNode.getParentNode();
                if (!parentNode.getNodeName().equals(REDNER_TAG)) {
                    int lineNumber = -1;
                    if (nameNode.getUserData(LINE_NUMBER_TAG) instanceof String) {
                        lineNumber = Integer.parseInt((String) nameNode.getUserData(LINE_NUMBER_TAG));
                    }
                    String name = nameNode.getTextContent();
                    result.put(lineNumber, name);
                }
            }
            //get <p klasse="redner" or <p klasse="N">
            NodeList paragraphNodeList = ((Element) node).getElementsByTagName(PARAGRAF_TAG);
            for (int j = 0; j < paragraphNodeList.getLength(); j++) {
                Node paragraphNode = paragraphNodeList.item(j);
                if (paragraphNode.getNodeType() == Node.ELEMENT_NODE) {
                    //<p> klasse = "redner"
                    if (checkAttribute((Element) paragraphNode, ATTRIBUTE_KLASSE_TAG, REDNER_TAG)) {
                        int lineNumber = Integer.parseInt((String) paragraphNode.getUserData(LINE_NUMBER_TAG));
                        Node rednerNode = ((Element) paragraphNode).getElementsByTagName(REDNER_TAG).item(0);
                        if (rednerNode.getNodeType() == Node.ELEMENT_NODE) {
                            String id = ((Element) rednerNode).getAttribute(SMALL_ID_TAG);
                            result.put(lineNumber, id);
                        }
                        //<p> klasse = "N"
                    } else if (checkAttribute((Element) paragraphNode, ATTRIBUTE_KLASSE_TAG, PARAGRAF_N_TAG)) {
                        int lineNumber = Integer.parseInt((String) paragraphNode.getUserData(LINE_NUMBER_TAG));
                        String content = paragraphNode.getTextContent();
                        result.put(lineNumber, content);
                    }
                }
            }

        }


        return result;
    }


    private List<RedeTeil> getAllRedeTeil(Node node) {
        List<RedeTeil> redeTeile = new ArrayList<>();
        if (node == null) {
            return redeTeile;
        }

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            //get all paragraphs <p>
            NodeList paragraphNodeList = ((Element) node).getElementsByTagName(PARAGRAF_TAG);

            for (int j = 0; j < paragraphNodeList.getLength(); j++) {
                RedeTeil redeTeil = getRedeTeil(paragraphNodeList.item(j));
                if (redeTeil != null) {
                    redeTeile.add(redeTeil);
                }
            }

            NodeList kommentarNodeList = ((Element) node).getElementsByTagName(KOMMENTAR_TAG);

            for (int k = 0; k < kommentarNodeList.getLength(); k++) {
                RedeTeil redeTeil = getRedeTeil(kommentarNodeList.item(k));
                if (redeTeil != null) {
                    redeTeile.add(redeTeil);
                }
            }

        }

        return ParseUtilities.listSort(redeTeile);
    }

    private boolean checkAttribute(Element element, String attributeName, String className) {
        String redeTeilAttribute = element.getAttribute(attributeName);
        if (redeTeilAttribute == null) {
            LOGGER.error("checkAttribute: attributeName: \" + attributeName + \"doesn't exist!");
            return false;
        }
        return redeTeilAttribute.equals(className);
    }

    private RedeTeil getRedeTeil(Node redeTeilNode) {
        if (redeTeilNode == null) {
            return null;
        }
        //ignore some attributes
        if (redeTeilNode.getNodeType() == Node.ELEMENT_NODE) {
            if (checkAttribute((Element) redeTeilNode, ATTRIBUTE_KLASSE_TAG, REDNER_TAG)) {
                return null;
            }
            if (checkAttribute((Element) redeTeilNode, ATTRIBUTE_KLASSE_TAG, PARAGRAF_N_TAG)) {
                return null;
            }
            if (checkAttribute((Element) redeTeilNode, ATTRIBUTE_KLASSE_TAG, DRUCKSACHE_TAG)) {
                return null;
            }
            if (checkAttribute((Element) redeTeilNode, ATTRIBUTE_KLASSE_TAG, T_FETT_TAG) || checkAttribute((Element) redeTeilNode, ATTRIBUTE_KLASSE_TAG, T_NAS_TAG)) {
                String content = redeTeilNode.getTextContent();
                if(content.contains("Drucksache")){
                    return null;
                }
            }
        }

        RedeTeilTyp redeTeilTyp;
        if (redeTeilNode.getNodeName().equals(PARAGRAF_TAG)) {
            redeTeilTyp = RedeTeilTyp.PARAGRAF;
        } else if (redeTeilNode.getNodeName().equals(KOMMENTAR_TAG)) {
            redeTeilTyp = RedeTeilTyp.KOMMENTAR;
        } else {
            redeTeilTyp = RedeTeilTyp.UNKNOWN;
        }
        int lineNumber = Integer.parseInt((String) redeTeilNode.getUserData(LINE_NUMBER_TAG));

        String text = redeTeilNode.getTextContent();

        return new RedeTeil(text, lineNumber, redeTeilTyp);
    }


    private List<Integer> getRednerIDList() {
        List<Integer> rednerIdList = new ArrayList<>();
        NodeList rednerNodeList = doc.getElementsByTagName(REDNER_LIST_TAG);

        for (int i = 0; i < rednerNodeList.getLength(); i++) {
            Node rednerListNode = rednerNodeList.item(i);
            if (rednerListNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) rednerListNode;
                NodeList redner = eElement.getElementsByTagName(REDNER_TAG);
                for (int j = 0; j < redner.getLength(); j++) {
                    Node rednerNode = redner.item(j);
                    String rednerID = ((Element) rednerNode).getAttribute(ATTRIBUTE_ID_TAG);
                    rednerIdList.add(Integer.parseInt(rednerID));
                }
            }
        }
        return rednerIdList;
    }

    private Person getPersonById(long id) {
        if (personBaseData.isEmpty()) {
            return null;
        }
        for (Person person :
                personBaseData) {
            if (person.getId() == id) {
                return person;
            }
        }

        return null;
    }

    private void createDoc(String path) {
        if (path == null || path.equals("")) {
            LOGGER.error("No file name was given");
            return;
        }
        try {
            file = new File(path);
            LOGGER.info(file.getAbsolutePath());

            if (!file.exists()) {
                LOGGER.error("File doesn't exist");
                return;
            }

           /* DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            doc = dbBuilder.parse(file);
            doc.getDocumentElement().normalize();*/

            doc = PositionalXMLReader.readXML(file.getAbsolutePath());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);

            transformer.transform(source, result);

                InputStream in = new ByteArrayInputStream (result.getWriter().toString().getBytes(StandardCharsets.UTF_8));
                doc = PositionalXMLReader.readXML(in);



        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    private String getPersonID(Element mdbElement) {
        String id = "";

        NodeList idNodeList = mdbElement.getElementsByTagName(ID_TAG);
        Node idNode = idNodeList.item(0);
        id = idNode.getTextContent();

        return id;
    }

    private Map<String, String> getBioData(Element mdbElement) {
        Map<String, String> bioDataProperties = new HashMap<>();

        NodeList bioDataNodeList = mdbElement.getElementsByTagName(BIO_DATA_TAG);
        Node bioDataNode = bioDataNodeList.item(0);

        if (bioDataNode.getNodeType() == Node.ELEMENT_NODE) {
            bioDataProperties.put(GEBURTSDATUM_TAG, ((Element) bioDataNode).getElementsByTagName(GEBURTSDATUM_TAG).item(0).getTextContent());
            bioDataProperties.put(GEBURTSORT_TAG, ((Element) bioDataNode).getElementsByTagName(GEBURTSORT_TAG).item(0).getTextContent());
            bioDataProperties.put(GESCHLECHT_TAG, ((Element) bioDataNode).getElementsByTagName(GESCHLECHT_TAG).item(0).getTextContent());
            bioDataProperties.put(FAMILIENSTAND_TAG, ((Element) bioDataNode).getElementsByTagName(FAMILIENSTAND_TAG).item(0).getTextContent());
            bioDataProperties.put(BERUF_TAG, ((Element) bioDataNode).getElementsByTagName(BERUF_TAG).item(0).getTextContent());
            bioDataProperties.put(RELIGION_TAG, ((Element) bioDataNode).getElementsByTagName(RELIGION_TAG).item(0).getTextContent());
        }

        return bioDataProperties;
    }

    private List<Fraktion> getFraktionen(Element mdbElement) {
        List<Fraktion> fraktionen = new ArrayList<>();

        NodeList wahlperiodeNodeList = mdbElement.getElementsByTagName(WAHLPERIODE_TAG);

        for (int i = 0; i < wahlperiodeNodeList.getLength(); i++) {
            Node wahlperiodeNode = wahlperiodeNodeList.item(i);
            if (wahlperiodeNode.getNodeType() == Node.ELEMENT_NODE) {
                String eintritt = ((Element) wahlperiodeNode).getElementsByTagName(WAHLPERIODE_VON_TAG).item(0).getTextContent();
                String austritt = ((Element) wahlperiodeNode).getElementsByTagName(WAHLPERIODE_BIS_TAG).item(0).getTextContent();
                NodeList institutionNodeList = ((Element) wahlperiodeNode).getElementsByTagName(INSTITUTION_TAG);
                for (int j = 0; j < institutionNodeList.getLength(); j++) {
                    Node institutionNode = institutionNodeList.item(j);
                    if (institutionNode.getNodeType() == Node.ELEMENT_NODE) {
                        String institutionArt = ((Element) institutionNode).getElementsByTagName(INS_ART_TAG).item(0).getTextContent();
                        if (institutionArt.equals(INSART_FRAKTION_TAG)) {
                            String institutionBeschreibung = ((Element) institutionNode).getElementsByTagName(INS_LANG_TAG).item(0).getTextContent();
                            Fraktion fraktion = new Fraktion(null, institutionBeschreibung, ParseUtilities.stringToDate(eintritt), ParseUtilities.stringToDate(austritt));
                            fraktionen.add(fraktion);
                        }
                    }
                }
            }
        }
        return fraktionen;
    }


    private Map<String, String> getNameProperties(Element mdbElement) {
        Map<String, String> nameProperties = new HashMap<>();

        NodeList nameNodeList = mdbElement.getElementsByTagName(NAME_TAG);

        for (int i = 0; i < nameNodeList.getLength(); i++) {
            Node nameNode = nameNodeList.item(i);
            if (nameNode.getNodeType() == Node.ELEMENT_NODE) {
                nameProperties.put(NACHNAME_TAG, ((Element) nameNode).getElementsByTagName(NACHNAME_TAG).item(0).getTextContent());
                nameProperties.put(VORNAME_TAG, ((Element) nameNode).getElementsByTagName(VORNAME_TAG).item(0).getTextContent());
                nameProperties.put(AKAD_TITEL_TAG, ((Element) nameNode).getElementsByTagName(AKAD_TITEL_TAG).item(0).getTextContent());
            }
        }
        return nameProperties;
    }
}
