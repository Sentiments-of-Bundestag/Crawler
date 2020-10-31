package model.Person;

import java.util.List;

public class Person {
    private final String titel;
    private final String vorname;
    private final String nachname;
    private final String beruf;
    private final String geschlecht;
    private final String geburtsdatum;
    private final String familienstand;
    private final String religion;
    private final String geburtsort;


    private final List<Fraktion> fraktionen;

    public Person(String titel, String vorname, String nachname, String beruf, String geschlecht, String geburtsdatum, String familienstand, String religion, String geburtsort, List<Fraktion> fraktionen) {

        this.titel = titel;
        this.vorname = vorname;
        this.nachname = nachname;
        this.geschlecht = geschlecht;
        this.geburtsdatum = geburtsdatum;
        this.familienstand = familienstand;
        this.religion = religion;
        this.beruf = beruf;
        this.geburtsort = geburtsort;
        this.fraktionen = fraktionen;
    }


    public List<Fraktion> getFraktionen() {
        return fraktionen;
    }

    public String getGeburtsort() {
        return geburtsort;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public String getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getFamilienstand() {
        return familienstand;
    }

    public String getReligion() {
        return religion;
    }

    public String getTitel() {
        return titel;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getBeruf() {
        return beruf;
    }

}
