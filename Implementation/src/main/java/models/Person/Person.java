package models.Person;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Document
public class Person {

    @Id
    long id;
    String titel;
    String vorname;
    String nachname;
    String beruf;
    String geschlecht;
    Date geburtsdatum;
    String familienstand;
    String religion;
    String geburtsort;
    List<Fraktion> fraktionen;

    public Person() {}

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

    public long getId() {
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

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public void setBeruf(String beruf) {
        this.beruf = beruf;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public void setFamilienstand(String familienstand) {
        this.familienstand = familienstand;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public void setGeburtsort(String geburtsort) {
        this.geburtsort = geburtsort;
    }

    public void setFraktionen(List<Fraktion> fraktionen) {
        this.fraktionen = fraktionen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return titel.equals(person.titel) &&
                vorname.equals(person.vorname) &&
                nachname.equals(person.nachname) &&
                beruf.equals(person.beruf) &&
                geschlecht.equals(person.geschlecht) &&
                geburtsdatum.equals(person.geburtsdatum) &&
                familienstand.equals(person.familienstand) &&
                religion.equals(person.religion) &&
                geburtsort.equals(person.geburtsort) &&
                fraktionen.equals(person.fraktionen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titel, vorname, nachname, beruf, geschlecht, geburtsdatum, familienstand, religion, geburtsort, fraktionen);
    }

    @Override
    public String toString() {
        return "Person{" +
                "titel='" + titel + '\'' +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", beruf='" + beruf + '\'' +
                ", geschlecht='" + geschlecht + '\'' +
                ", geburtsdatum='" + geburtsdatum + '\'' +
                ", familienstand='" + familienstand + '\'' +
                ", religion='" + religion + '\'' +
                ", geburtsort='" + geburtsort + '\'' +
                ", fraktionen=" + fraktionen +
                '}';
    }
}
