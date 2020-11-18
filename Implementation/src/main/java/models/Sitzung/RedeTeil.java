package models.Sitzung;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class RedeTeil {
    String text;
    int zeileNr;
    String typ;

    public RedeTeil() {}

    public RedeTeil(String text, int zeileNr, String typ) {
        this.text = text;
        this.zeileNr = zeileNr;
        this.typ = typ;
    }

    public String getText() {
        return text;
    }

    public int getZeileNr() {
        return zeileNr;
    }

    public String getTyp() {
        return typ;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setZeileNr(int zeileNr) {
        this.zeileNr = zeileNr;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RedeTeil)) return false;
        RedeTeil redeTeil = (RedeTeil) o;
        return zeileNr == redeTeil.zeileNr &&
                text.equals(redeTeil.text) &&
                typ.equals(redeTeil.typ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, zeileNr, typ);
    }

    @Override
    public String toString() {
        return "RedeTeil{" +
                "text='" + text + '\'' +
                ", zeileNr=" + zeileNr +
                ", typ='" + typ + '\'' +
                '}';
    }
}
