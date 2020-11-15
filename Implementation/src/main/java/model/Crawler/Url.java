package model.Crawler;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Objects;

public class Url {

    @Id
    int id;

    String host;

    String urlValue;

    Date lastRequestTime;

    int lastStatusCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrlValue() {
        return urlValue;
    }

    public void setUrlValue(String urlValue) {
        this.urlValue = urlValue;
    }

    public Date getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(Date lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public void setLastStatusCode(int lastStatusCode) {
        this.lastStatusCode = lastStatusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Url)) return false;
        Url url = (Url) o;
        return id == url.id &&
                lastStatusCode == url.lastStatusCode &&
                host.equals(url.host) &&
                urlValue.equals(url.urlValue) &&
                lastRequestTime.equals(url.lastRequestTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, urlValue, lastRequestTime, lastStatusCode);
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", urlValue='" + urlValue + '\'' +
                ", lastRequestTime=" + lastRequestTime +
                ", lastStatusCode=" + lastStatusCode +
                '}';
    }
}
