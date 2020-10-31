package model.Person;

public class Fraktion {
    private final String name;
    private final String beschreibung;

    public Fraktion(String name, String beschreibung) {
        this.name = name;
        this.beschreibung = beschreibung;
    }

    public String getName() {
        return name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }
}
