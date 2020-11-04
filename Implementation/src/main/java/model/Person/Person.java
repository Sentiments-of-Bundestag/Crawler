package model.Person;

import java.util.Date;
import java.util.List;

public class Person {
    private final int id;
    private final String titel;
    private final String vorname;
    private final String nachname;
    private final String beruf;
    private final String geschlecht;
    private final Date geburtsdatum;
    private final String familienstand;
    private final String religion;
    private final String geburtsort;


    private final List<Fraktion> fraktionen;

    public Person(int id, String titel, String vorname, String nachname, String beruf, String geschlecht, Date geburtsdatum, String familienstand, String religion, String geburtsort, List<Fraktion> fraktionen) {
        this.id = id;
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

    public int getId() {
        return id;
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

    public Date getGeburtsdatum() {
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
