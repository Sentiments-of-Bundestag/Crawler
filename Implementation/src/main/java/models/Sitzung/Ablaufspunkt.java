package model.Sitzung;

import java.util.List;

public class Ablaufspunkt {
    private final AblaufspunktTyp ablauf_typ;
    private final String thema;
    private final int zeile_nr;
    private final List<Rede> reden;

    public Ablaufspunkt(AblaufspunktTyp ablauf_typ, String thema, int zeile_nr, List<Rede> reden) {
        this.ablauf_typ = ablauf_typ;
        this.thema = thema;
        this.zeile_nr = zeile_nr;
        this.reden = reden;
    }

    public AblaufspunktTyp getAblauf_typ() {
        return ablauf_typ;
    }

    public String getThema() {
        return thema;
    }

    public int getZeile_nr() {
        return zeile_nr;
    }

    public List<Rede> getReden() {
        return reden;
    }
}
