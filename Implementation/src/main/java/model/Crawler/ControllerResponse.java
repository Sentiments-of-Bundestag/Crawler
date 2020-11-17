package model.Crawler;

import java.util.Objects;
import java.util.Set;

public class ControllerResponse {
    String title;
    Set <PlanedTask> body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<PlanedTask> getBody() {
        return body;
    }

    public void setBody(Set<PlanedTask> body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControllerResponse)) return false;
        ControllerResponse that = (ControllerResponse) o;
        return title.equals(that.title) &&
                body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body);
    }

    @Override
    public String toString() {
        return "ControllerResponse{" +
                "title='" + title + '\'' +
                ", body=" + body +
                '}';
    }
}
