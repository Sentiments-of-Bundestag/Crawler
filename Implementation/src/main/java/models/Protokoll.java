package model;

import model.Person.Person;
import model.Sitzung.Sitzungsverlauf;

import java.util.Date;
import java.util.List;

public class Protokoll {
    private final int id;
    private final String ort;
    private final Date naechste_sitzung;
    private final Date sitzung_datum;
    private final String issn;
    private final String berichtart;
    private final List<Person> rednerListe;
    private final Sitzungsverlauf sitzungsverlauf;

    public Protokoll(int id, String ort, Date naechste_sitzung, Date sitzung_datum, String issn, String berichtart, List<Person> rednerListe, Sitzungsverlauf sitzungsverlauf) {
        this.id = id;
        this.ort = ort;
        this.naechste_sitzung = naechste_sitzung;
        this.sitzung_datum = sitzung_datum;
        this.issn = issn;
        this.berichtart = berichtart;
        this.rednerListe = rednerListe;
        this.sitzungsverlauf = sitzungsverlauf;
    }

    public Sitzungsverlauf getSitzungsverlauf() {
        return sitzungsverlauf;
    }

    public List<Person> getRednerListe() {
        return rednerListe;
    }

    public int getId() {
        return id;
    }

    public String getOrt() {
        return ort;
    }

    public Date getNaechste_sitzung() {
        return naechste_sitzung;
    }

    public Date getSitzung_datum() {
        return sitzung_datum;
    }

    public String getIssn() {
        return issn;
    }

    public String getBerichtart() {
        return berichtart;
    }
}
