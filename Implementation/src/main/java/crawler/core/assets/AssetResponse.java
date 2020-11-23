package crawler.core.assets;

/**
 * The response of an asset fetch.
 *
 */
public class AssetResponse {

    String url;
    String referer;
    String assetPath;
    int responseCode;
    long fetchTime;
    int assetSize;

    public AssetResponse() {}

    public AssetResponse(String url, String referer, String assetPath, int responseCode, long fetchTime, int assetSize) {
        super();
        this.url = url;
        this.assetPath = assetPath;
        this.responseCode = responseCode;
        this.fetchTime = fetchTime;
        this.referer = referer;
        this.assetSize = assetSize;
    }

    /**
     * Get the URL of the asset.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * The response code when the asset was fetched.
     *
     * @return the response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    public String getAssetPath() { return assetPath; }

    public long getFetchTime() {
        return fetchTime;
    }

    public String getReferer() {
        return referer;
    }

    public int getAssetSize() { return assetSize; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + responseCode;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((assetPath == null) ? 0 : assetPath.hashCode());
        result = (int) (prime * result + fetchTime);
        result = prime * result + ((referer == null) ? 0 : referer.hashCode());
        result = prime * result + assetSize;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AssetResponse other = (AssetResponse) obj;
        if (responseCode != other.responseCode) return false;
        if (fetchTime != other.fetchTime) return false;
        if (assetSize != other.assetSize) return false;
        if (url == null) {
            if (other.url != null) return false;
        } else if (!url.equals(other.url)) return false;
        if (assetPath == null) {
            if (other.assetPath != null) return false;
        } else if (!assetPath.equals(other.assetPath)) return false;
        if (referer == null) {
            if (other.referer != null) return false;
        } else if (!referer.equals(other.referer)) return false;
        return true;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public void setAssetSize(int assetSize) {
        this.assetSize = assetSize;
    }
}