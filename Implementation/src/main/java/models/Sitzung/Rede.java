package models.Sitzung;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Rede {
    @Id
    String redeId;
    int zeileNr;
    List<RedeTeil> redeInhalt;
    int rednerId;

    public Rede(String redeId, int zeileNr, List<RedeTeil> redeInhalt, int rednerId) {
        this.redeId = redeId;
        this.zeileNr = zeileNr;
        this.redeInhalt = redeInhalt;
        this.rednerId = rednerId;
    }

    public String getRedeId() {
        return redeId;
    }

    public int getZeileNr() {
        return zeileNr;
    }

    public List<RedeTeil> getRedeInhalt() {
        return redeInhalt;
    }

    public int getRednerId() {
        return rednerId;
    }
}
