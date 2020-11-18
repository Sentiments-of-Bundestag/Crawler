package models.Crawler;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document
public class PlanedTask {
    @Id
    int id;

    String name;

    Url url;

    String type;

    String value;

    Date planedTime;

    public PlanedTask() {
    }

    public PlanedTask(int id, String name, Url url, String type, String value, Date planedTime) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.type = type;
        this.value = value;
        this.planedTime = planedTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getPlanedTime() {
        return planedTime;
    }

    public void setPlanedTime(Date planedTime) {
        this.planedTime = planedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanedTask)) return false;
        PlanedTask that = (PlanedTask) o;
        return id == that.id &&
                name.equals(that.name) &&
                url.equals(that.url) &&
                type.equals(that.type) &&
                value.equals(that.value) &&
                planedTime.equals(that.planedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, type, value, planedTime);
    }

    @Override
    public String toString() {
        return "PlanedTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url=" + url +
                ", type='" + type + '\'' +
                ", status='" + value + '\'' +
                ", planedTime=" + planedTime +
                '}';
    }
}
