package models.Sitzung;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document
public class Ablaufspunkt {
    String ablaufTyp;
    String thema;
    int zeile_nr;
    List<Rede> reden;

    public Ablaufspunkt(String ablaufTyp, String thema, int zeileNr, List<Rede> reden) {
        this.ablaufTyp = ablaufTyp;
        this.thema = thema;
        this.zeile_nr = zeileNr;
        this.reden = reden;
    }

    public Ablaufspunkt() {
    }

    public String getAblaufTyp() {
        return ablaufTyp;
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

    public void setAblaufTyp(String ablaufTyp) {
        this.ablaufTyp = ablaufTyp;
    }

    public void setThema(String thema) {
        this.thema = thema;
    }

    public void setZeile_nr(int zeile_nr) {
        this.zeile_nr = zeile_nr;
    }

    public void setReden(List<Rede> reden) {
        this.reden = reden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ablaufspunkt)) return false;
        Ablaufspunkt that = (Ablaufspunkt) o;
        return zeile_nr == that.zeile_nr &&
                ablaufTyp.equals(that.ablaufTyp) &&
                thema.equals(that.thema) &&
                reden.equals(that.reden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ablaufTyp, thema, zeile_nr, reden);
    }

    @Override
    public String toString() {
        return "Ablaufspunkt{" +
                "ablaufTyp='" + ablaufTyp + '\'' +
                ", thema='" + thema + '\'' +
                ", zeile_nr=" + zeile_nr +
                ", reden=" + reden +
                '}';
    }
}
