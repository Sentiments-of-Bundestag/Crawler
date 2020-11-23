package models;

import models.Person.Redner;
import models.Sitzung.Sitzungsverlauf;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Document
public class Protokoll {
    @Id
    int id;
    String ort;
    Date naechsteSitzung;
    Date sitzungDatum;
    String issn;
    String berichtart;
    List<Redner> rednerListe;
    Sitzungsverlauf sitzungsverlauf;

    public Protokoll() {}

    public Protokoll(int id, String ort, Date naechsteSitzung, Date sitzungDatum, String issn, String berichtart, List<Redner> rednerListe, Sitzungsverlauf sitzungsverlauf) {
        this.id = id;
        this.ort = ort;
        this.naechsteSitzung = naechsteSitzung;
        this.sitzungDatum = sitzungDatum;
        this.issn = issn;
        this.berichtart = berichtart;
        this.rednerListe = rednerListe;
        this.sitzungsverlauf = sitzungsverlauf;
    }

    public Sitzungsverlauf getSitzungsverlauf() {
        return sitzungsverlauf;
    }

    public List<Redner> getRednerListe() {
        return rednerListe;
    }

    public int getId() {
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

    public String getBerichtart() {
        return berichtart;
    }

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

    public void setBerichtart(String berichtart) {
        this.berichtart = berichtart;
    }

    public void setRednerListe(List<Redner> rednerListe) {
        this.rednerListe = rednerListe;
    }

    public void setSitzungsverlauf(Sitzungsverlauf sitzungsverlauf) {
        this.sitzungsverlauf = sitzungsverlauf;
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
                berichtart.equals(protokoll.berichtart) &&
                rednerListe.equals(protokoll.rednerListe) &&
                sitzungsverlauf.equals(protokoll.sitzungsverlauf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ort, naechsteSitzung, sitzungDatum, issn, berichtart, rednerListe, sitzungsverlauf);
    }

    @Override
    public String toString() {
        return "Protokoll{" +
                "id=" + id +
                ", ort='" + ort + '\'' +
                ", naechsteSitzung=" + naechsteSitzung +
                ", sitzungDatum=" + sitzungDatum +
                ", issn='" + issn + '\'' +
                ", berichtart='" + berichtart + '\'' +
                ", rednerListe=" + rednerListe +
                ", sitzungsverlauf=" + sitzungsverlauf +
                '}';
    }
}
