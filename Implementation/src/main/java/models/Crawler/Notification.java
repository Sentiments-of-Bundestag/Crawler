package models.Crawler;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Notification {
    Set<Integer> ids;

    public Notification() {}

    public Notification(Set<Integer> ids) {
        this.ids = ids;
        this.ids = this.ids.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toCollection(LinkedHashSet::new));
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
