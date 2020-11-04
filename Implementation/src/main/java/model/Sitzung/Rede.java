package model.Sitzung;

import java.util.List;

public class Rede {
    private final String rede_nr;
    private final int zeile_nr;
    private final List<RedeTeil> redeInhalt;
    private final int redner_id;

    public Rede(String rede_nr, int zeile_nr, List<RedeTeil> redeInhalt, int redner_id) {
        this.rede_nr = rede_nr;
        this.zeile_nr = zeile_nr;
        this.redeInhalt = redeInhalt;
        this.redner_id = redner_id;
    }

    public String getRede_nr() {
        return rede_nr;
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
