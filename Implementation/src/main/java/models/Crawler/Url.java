package models.Crawler;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

@Document
public class Url {

    @Id
    String value;

    String host;

    Date lastRequestTime;

    int lastStatusCode;

    String type;

    String downloadedAsset;

    int downloadedAssetSize;

    public Url() {}

    public Url(String url) {
        try {
            URL httURL = new URL(url);
            this.host = httURL.getHost();
            this.value = url;
            this.lastRequestTime = null;
            this.lastStatusCode = -1;
            this.downloadedAssetSize = -1;
            this.downloadedAsset = null;
            this.type = "Page";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Url(String host, String value, Date lastRequestTime, int lastStatusCode, String downloadedAsset, int downloadedAssetSize, String type) {
        this.host = host;
        this.value = value;
        this.lastRequestTime = lastRequestTime;
        this.lastStatusCode = lastStatusCode;
        this.downloadedAsset = downloadedAsset;
        this.downloadedAssetSize = downloadedAssetSize;
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getDownloadedAsset() {
        return downloadedAsset;
    }

    public void setDownloadedAsset(String downloadedAsset) {
        this.downloadedAsset = downloadedAsset;
    }

    public int getDownloadedAssetSize() {
        return downloadedAssetSize;
    }

    public void setDownloadedAssetSize(int downloadedAssetSize) {
        this.downloadedAssetSize = downloadedAssetSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Url)) return false;
        Url url = (Url) o;
        return lastStatusCode == url.lastStatusCode &&
                downloadedAssetSize == url.downloadedAssetSize &&
                value.equals(url.value) &&
                host.equals(url.host) &&
                lastRequestTime.equals(url.lastRequestTime) &&
                type.equals(url.type) &&
                downloadedAsset.equals(url.downloadedAsset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, host, lastRequestTime, lastStatusCode, type, downloadedAsset, downloadedAssetSize);
    }

    @Override
    public String toString() {
        return "Url{" +
                "value='" + value + '\'' +
                ", host='" + host + '\'' +
                ", lastRequestTime=" + lastRequestTime +
                ", lastStatusCode=" + lastStatusCode +
                ", type='" + type + '\'' +
                ", downloadedAsset='" + downloadedAsset + '\'' +
                ", downloadedAssetSize=" + downloadedAssetSize +
                '}';
    }
}
