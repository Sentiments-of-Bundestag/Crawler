package models.Person;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document
public class Fraktion {
    @Id
    String name;
    String beschreibung;
    Date eintrittsDatum;
    Date austrittsDatum;

    public Fraktion() {}

    public Fraktion(String name, String beschreibung, Date eintrittsDatum, Date austrittsDatum) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.eintrittsDatum = eintrittsDatum;
        this.austrittsDatum = austrittsDatum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Date getEintrittsDatum() {
        return eintrittsDatum;
    }

    public void setEintrittsDatum(Date eintrittsDatum) {
        this.eintrittsDatum = eintrittsDatum;
    }

    public Date getAustrittsDatum() {
        return austrittsDatum;
    }

    public void setAustrittsDatum(Date austrittsDatum) {
        this.austrittsDatum = austrittsDatum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fraktion)) return false;
        Fraktion fraktion = (Fraktion) o;
        return name.equals(fraktion.name) &&
                beschreibung.equals(fraktion.beschreibung) &&
                eintrittsDatum.equals(fraktion.eintrittsDatum) &&
                austrittsDatum.equals(fraktion.austrittsDatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, beschreibung, eintrittsDatum, austrittsDatum);
    }

    @Override
    public String toString() {
        return "Fraktion{" +
                "name='" + name + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", eintrittsDatum=" + eintrittsDatum +
                ", austrittsDatum=" + austrittsDatum +
                '}';
    }
}
