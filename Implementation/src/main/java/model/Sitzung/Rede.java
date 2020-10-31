package model.Sitzung;

import model.Person.Redner;

import java.util.List;

public class Rede {
    private final String rede_id;
    private final int zeile_nr;
    private final List<RedeTeil> redeInhalt;
    private final int redner_id;

    public Rede(String rede_id, int zeile_nr, List<RedeTeil> redeInhalt, int redner_id) {
        this.rede_id = rede_id;
        this.zeile_nr = zeile_nr;
        this.redeInhalt = redeInhalt;
        this.redner_id = redner_id;
    }

    public String getRede_id() {
        return rede_id;
    }

    public int getZeile_nr() {
        return zeile_nr;
    }

    public List<RedeTeil> getRedeInhalt() {
        return redeInhalt;
    }

    public int getRedner_id() {
        return redner_id;
    }
}
