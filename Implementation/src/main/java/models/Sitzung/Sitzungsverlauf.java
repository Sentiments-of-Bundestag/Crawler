package model.Sitzung;

import java.util.Date;
import java.util.List;

public class Sitzungsverlauf {
    private final int protokoll_id;
    private final Date sitzung_start;
    private final Date sitzung_ende;
    private final List<Ablaufspunkt> ablaufspunkte;

    public Sitzungsverlauf(int protokoll_id, Date sitzung_start, Date sitzung_ende, List<Ablaufspunkt> ablaufspunkte) {
        this.protokoll_id = protokoll_id;
        this.sitzung_start = sitzung_start;
        this.sitzung_ende = sitzung_ende;
        this.ablaufspunkte = ablaufspunkte;
    }

    public int getProtokoll_id() {
        return protokoll_id;
    }

    public Date getSitzung_start() {
        return sitzung_start;
    }

    public Date getSitzung_ende() {
        return sitzung_ende;
    }

    public List<Ablaufspunkt> getAblaufspunkte() {
        return ablaufspunkte;
    }
}
