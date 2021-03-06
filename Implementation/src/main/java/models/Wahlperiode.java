package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Document
public class Wahlperiode {
    @Id
    long id;
    Date anfangDatum;
    Date endeDatum;
    Set<Protokoll> protokolle;

    public Wahlperiode() {}

    public Wahlperiode(int id, Date anfangDatum, Date endeDatum, Set<Protokoll> protokolle){
        this.id = id;
        this.anfangDatum = anfangDatum;
        this.endeDatum = endeDatum;
        this.protokolle = protokolle;
    }

    public Set<Protokoll> getProtokolle() {
        return protokolle;
    }

    public long getId() {
        return id;
    }

    public Date getAnfangDatum() {
        return anfangDatum;
    }

    public Date getEndeDatum() {
        return endeDatum;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAnfangDatum(Date anfangDatum) {
        this.anfangDatum = anfangDatum;
    }

    public void setEndeDatum(Date endeDatum) {
        this.endeDatum = endeDatum;
    }

    public void setProtokolle(Set<Protokoll> protokolle) {
        this.protokolle = protokolle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wahlperiode)) return false;
        Wahlperiode that = (Wahlperiode) o;
        return id == that.id &&
                anfangDatum.equals(that.anfangDatum) &&
                endeDatum.equals(that.endeDatum) &&
                protokolle.equals(that.protokolle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, anfangDatum, endeDatum, protokolle);
    }

    @Override
    public String toString() {
        return "Wahlperiode{" +
                "id=" + id +
                ", anfangDatum=" + anfangDatum +
                ", endeDatum=" + endeDatum +
                ", protokolle=" + protokolle +
                '}';
    }
}
