package crawler.core.assets;

/**
 * The response of an asset fetch.
 *
 */
public class AssetResponse {

    private final String url;
    private final String referer;
    private final int responseCode;
    private final long fetchTime;

    public AssetResponse(String url, String referer, int responseCode, long fetchTime) {
        super();
        this.url = url;
        this.responseCode = responseCode;
        this.fetchTime = fetchTime;
        this.referer = referer;
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

    public long getFetchTime() {
        return fetchTime;
    }

    public String getReferer() {
        return referer;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + responseCode;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AssetResponse other = (AssetResponse) obj;
        if (responseCode != other.responseCode) return false;
        if (url == null) {
            if (other.url != null) return false;
        } else if (!url.equals(other.url)) return false;
        return true;
    }
}