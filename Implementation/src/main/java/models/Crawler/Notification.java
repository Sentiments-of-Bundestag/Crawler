package models.Crawler;

import java.util.Objects;
import java.util.Set;

public class Notification {
    Set<Integer> ids;

    public Notification() {}

    public Notification(Set<Integer> ids) {
        this.ids = ids;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "ids=" + ids +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return ids.equals(that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }
}
