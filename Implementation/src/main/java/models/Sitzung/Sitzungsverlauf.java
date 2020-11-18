package models.Sitzung;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Document
public class Sitzungsverlauf {
    @Id
    int protokollId;
    Date sitzungStart;
    Date sitzungEnde;
    List<Ablaufspunkt> ablaufspunkte;

    public Sitzungsverlauf() {}

    public Sitzungsverlauf(int protokollId, Date sitzungStart, Date sitzungEnde, List<Ablaufspunkt> ablaufspunkte) {
        this.protokollId = protokollId;
        this.sitzungStart = sitzungStart;
        this.sitzungEnde = sitzungEnde;
        this.ablaufspunkte = ablaufspunkte;
    }

    public int getProtokollId() {
        return protokollId;
    }

    public Date getSitzungStart() {
        return sitzungStart;
    }

    public Date getSitzungEnde() {
        return sitzungEnde;
    }

    public List<Ablaufspunkt> getAblaufspunkte() {
        return ablaufspunkte;
    }

    public void setProtokollId(int protokollId) {
        this.protokollId = protokollId;
    }

    public void setSitzungStart(Date sitzungStart) {
        this.sitzungStart = sitzungStart;
    }

    public void setSitzungEnde(Date sitzungEnde) {
        this.sitzungEnde = sitzungEnde;
    }

    public void setAblaufspunkte(List<Ablaufspunkt> ablaufspunkte) {
        this.ablaufspunkte = ablaufspunkte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sitzungsverlauf)) return false;
        Sitzungsverlauf that = (Sitzungsverlauf) o;
        return protokollId == that.protokollId &&
                sitzungStart.equals(that.sitzungStart) &&
                sitzungEnde.equals(that.sitzungEnde) &&
                ablaufspunkte.equals(that.ablaufspunkte);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protokollId, sitzungStart, sitzungEnde, ablaufspunkte);
    }

    @Override
    public String toString() {
        return "Sitzungsverlauf{" +
                "protokollId=" + protokollId +
                ", sitzungStart=" + sitzungStart +
                ", sitzungEnde=" + sitzungEnde +
                ", ablaufspunkte=" + ablaufspunkte +
                '}';
    }
}
