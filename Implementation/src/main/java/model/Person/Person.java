package model.Person;

import java.util.List;

public class Person {
    private final String titel;
    private final String vorname;
    private final String nachname;
    private final String namenszusatz;
    private final String ortszusatz;
    private final String rolle;
    private final String bdland;
    private final List<Fraktion> fraktionen;

    public Person(String titel, String vorname, String nachname, String namenszusatz, String ortszusatz, String rolle, String bdland, List<Fraktion> fraktionen) {

        this.titel = titel;
        this.vorname = vorname;
        this.nachname = nachname;
        this.namenszusatz = namenszusatz;
        this.ortszusatz = ortszusatz;
        this.rolle = rolle;
        this.bdland = bdland;
        this.fraktionen = fraktionen;
    }

    public List<Fraktion> getFraktionen() {
        return fraktionen;
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

    public String getNamenszusatz() {
        return namenszusatz;
    }

    public String getOrtszusatz() {
        return ortszusatz;
    }

    public String getRolle() {
        return rolle;
    }

    public String getBdland() {
        return bdland;
    }
}
