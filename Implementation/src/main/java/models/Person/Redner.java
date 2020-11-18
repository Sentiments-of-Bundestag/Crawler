package models.Person;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class Redner {
    @Id
    int id;
    Person person;

    public Redner() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Redner)) return false;
        Redner redner = (Redner) o;
        return id == redner.id &&
                person.equals(redner.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, person);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Redner{" +
                "id=" + id +
                ", person=" + person +
                '}';
    }
}
