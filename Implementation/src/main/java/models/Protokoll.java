package models;

import models.Person.Person;
import models.Sitzung.Sitzungsverlauf;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Document
public class Protokoll {
    @Id
    long id;
    String ort;
    Date naechsteSitzung;
    Date sitzungDatum;
    String issn;
    Set<Person> rednerListe;
    Sitzungsverlauf sitzungsverlauf;
    boolean notified;

    public Protokoll() {}

    public Protokoll(long id, String ort, Date naechsteSitzung, Date sitzungDatum, String issn, Set<Person> rednerListe, Sitzungsverlauf sitzungsverlauf) {
        this.id = id;
        this.ort = ort;
        this.naechsteSitzung = naechsteSitzung;
        this.sitzungDatum = sitzungDatum;
        this.issn = issn;
        this.rednerListe = rednerListe;
        this.sitzungsverlauf = sitzungsverlauf;
            }

    public Sitzungsverlauf getSitzungsverlauf() {
        return sitzungsverlauf;
    }

    public Set<Person> getRednerListe() {
        return rednerListe;
    }

    public long getId() {
        return id;
    }

    public String getOrt() {
        return ort;
    }

    public Date getNaechsteSitzung() {
        return naechsteSitzung;
    }

    public Date getSitzungDatum() {
        return sitzungDatum;
    }

    public String getIssn() {
        return issn;
    }


    public boolean getNotified() { return notified; }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public void setNaechsteSitzung(Date naechsteSitzung) {
        this.naechsteSitzung = naechsteSitzung;
    }

    public void setSitzungDatum(Date sitzungDatum) {
        this.sitzungDatum = sitzungDatum;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public void setRednerListe(Set<Person> rednerListe) {
        this.rednerListe = rednerListe;
    }

    public void setSitzungsverlauf(Sitzungsverlauf sitzungsverlauf) {
        this.sitzungsverlauf = sitzungsverlauf;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Protokoll)) return false;
        Protokoll protokoll = (Protokoll) o;
        return id == protokoll.id &&
                ort.equals(protokoll.ort) &&
                naechsteSitzung.equals(protokoll.naechsteSitzung) &&
                sitzungDatum.equals(protokoll.sitzungDatum) &&
                issn.equals(protokoll.issn) &&
                rednerListe.equals(protokoll.rednerListe) &&
                sitzungsverlauf.equals(protokoll.sitzungsverlauf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ort, naechsteSitzung, sitzungDatum, issn, rednerListe, sitzungsverlauf);
    }

    @Override
    public String toString() {
        return "Protokoll{" +
                "id=" + id +
                ", ort='" + ort + '\'' +
                ", naechsteSitzung=" + naechsteSitzung +
                ", sitzungDatum=" + sitzungDatum +
                ", issn='" + issn + '\'' +
                ", rednerListe=" + rednerListe +
                ", sitzungsverlauf=" + sitzungsverlauf +
                '}';
    }
}
