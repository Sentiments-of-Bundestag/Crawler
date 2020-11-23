package models.Crawler;

import java.util.Objects;

public class ControllerRequest {
    int id;
    int frequency;
    String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControllerRequest)) return false;
        ControllerRequest that = (ControllerRequest) o;
        return id == that.id &&
                frequency == that.frequency &&
                url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frequency, url);
    }

    @Override
    public String toString() {
        return "ControllerRequest{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", url='" + url + '\'' +
                '}';
    }
}
