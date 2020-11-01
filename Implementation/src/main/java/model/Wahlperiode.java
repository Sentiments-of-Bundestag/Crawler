package model;

import java.util.Date;
import java.util.List;

public class Wahlperiode {
    private final int id;
    private final Date anfang_datum;
    private final Date ende_datum;
    private final List<Protokoll> protokolle;

    public Wahlperiode(int id, Date anfang_datum, Date ende_datum, List<Protokoll> protokolle){
        this.id = id;
        this.anfang_datum = anfang_datum;
        this.ende_datum = ende_datum;
        this.protokolle = protokolle;
    }

    public List<Protokoll> getProtokolle() {
        return protokolle;
    }

    public int getId() {
        return id;
    }

    public Date getAnfang_datum() {
        return anfang_datum;
    }

    public Date getEnde_datum() {
        return ende_datum;
    }
}
