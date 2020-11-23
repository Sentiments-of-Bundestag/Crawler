package model.Sitzung;

public class RedeTeil {
    private final String text;
    private final int zeile_nr;
    private final RedeTeilTyp typ;

    public RedeTeil(String text, int zeile_nr, RedeTeilTyp typ) {
        this.text = text;
        this.zeile_nr = zeile_nr;
        this.typ = typ;
    }

    public String getText() {
        return text;
    }

    public int getZeile_nr() {
        return zeile_nr;
    }

    public RedeTeilTyp getTyp() {
        return typ;
    }
}
