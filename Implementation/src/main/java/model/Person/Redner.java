package model.Person;

import model.Person.Person;

public class Redner {
    private final int id;
    private final Person person;

    public Redner(int id, Person person) {
        this.id = id;
        this.person = person;
    }

    public int getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }
}
