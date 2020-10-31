package model.Person;

import java.util.Date;

public class Fraktion {
    private final String name;
    private final String beschreibung;
    private final Date eintrittsDatum;
    private final Date austrittsDatum;

    public Fraktion(String name, String beschreibung, Date eintrittsDatum, Date austrittsDatum) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.eintrittsDatum = eintrittsDatum;
        this.austrittsDatum = austrittsDatum;
    }

    public Date getEintrittsDatum() {
        return eintrittsDatum;
    }

    public Date getAustrittsDatum() {
        return austrittsDatum;
    }

    public String getName() {
        return name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }
}
