package crawler.core;

import crawler.utils.HeaderUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration for a crawl.
 *
 */
public final class CrawlerConfiguration {

    /**
     * System property for max number of http threads.
     */
    public final static String MAX_THREADS_PROPERTY_NAME = "crawler.nrofhttpthreads";
    /**
     * System property for socket timeout (in ms).
     */
    public final static String SOCKET_TIMEOUT_PROPERTY_NAME =
            "crawler.http.socket.timeout";
    /**
     * System property for connection timeout (in ms).
     */
    public final static String CONNECTION_TIMEOUT_PROPERTY_NAME =
            "crawler.http.connection.timeout";

    /**
     * System property for auth.
     */
    public final static String AUTH_PROPERTY_NAME = "crawler.auth";

    /**
     * System property for proxy config.
     */
    public final static String PROXY_PROPERTY_NAME = "crawler.proxy";

    /**
     * System property for file filters.
     */
    public final static String FILE_FILTERS_PROPERTY_NAME = "crawler.file.filters";

    /**
     * System property for download location.
     */
    public final static String DEFAULT_DOWNLOAD_FILE_LOCATION_PROPERTY_NAME = "crawler.download.location";

    /**
     * System property for stammdaten filename.
     */
    public final static String DEFAULT_STAMMDATEN_FILENAME = "crawler.default.stammdaten.filename";

    /**
     * The default crawl level if no is supplied.
     */
    public static final int DEFAULT_CRAWL_LEVEL = 1;

    /**
     * The default value if url:s should be verified to be ok or not.
     */
    public static final boolean DEFAULT_SHOULD_VERIFY_URLS = true;

    private int maxLevels = DEFAULT_CRAWL_LEVEL;
    private String notOnPath = "";
    private String onlyOnPath = "";
    private String requestHeaders = "";
    private String fileFilters = "";
    private String downloadFileLocation = "";

    private String stammdatenFilename = "";
    private String startUrl = "";
    private Map<String, String> requestHeadersMap = Collections.emptyMap();

    private boolean verifyUrls = DEFAULT_SHOULD_VERIFY_URLS;

    private CrawlerConfiguration() {

    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getRequestHeadersMap() {
        return requestHeadersMap;
    }

    public Map<String, String> getRequestHeadersMap(String requestHeaders) {
        this.setRequestHeaders(requestHeaders);
        return requestHeadersMap;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public String getNotOnPath() {
        return notOnPath;
    }

    public String getOnlyOnPath() {
        return onlyOnPath;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public String getFileFilters() {
        return fileFilters;
    }

    public String getDownloadFileLocation() {
        return downloadFileLocation;
    }

    public boolean isVerifyUrls() {
        return verifyUrls;
    }

    public String getStammdatenFilename() {
        return stammdatenFilename;
    }
    private CrawlerConfiguration copy() {
        final CrawlerConfiguration conf = new CrawlerConfiguration();
        conf.setMaxLevels(getMaxLevels());
        conf.setNotOnPath(getNotOnPath());
        conf.setOnlyOnPath(getOnlyOnPath());
        conf.setStartUrl(getStartUrl());
        conf.setVerifyUrls(isVerifyUrls());
        conf.setFileFilters(getFileFilters());
        conf.setRequestHeaders(getRequestHeaders());
        conf.setDownloadFileLocation(getDownloadFileLocation());
        conf.setStammdatenFilename(getStammdatenFilename());
        return conf;
    }

    private void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
        requestHeadersMap = HeaderUtil.getInstance().createHeadersFromString(requestHeaders);
    }

    private void setMaxLevels(int maxLevels) {
        this.maxLevels = maxLevels;
    }

    private void setNotOnPath(String notOnPath) {
        this.notOnPath = notOnPath;
    }

    private void setOnlyOnPath(String onlyOnPath) {
        this.onlyOnPath = onlyOnPath;
    }

    private void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    private void setFileFilters(String fileFilters) {
        this.fileFilters = fileFilters;
    }

    private void setDownloadFileLocation(String downloadFileLocation) {
        this.downloadFileLocation = downloadFileLocation;
    }

    private void setVerifyUrls(boolean verifyUrls) {
        this.verifyUrls = verifyUrls;
    }

    public void setStammdatenFilename(String stammdatenFilename) {
        this.stammdatenFilename = stammdatenFilename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrawlerConfiguration)) return false;
        CrawlerConfiguration that = (CrawlerConfiguration) o;
        return maxLevels == that.maxLevels &&
                verifyUrls == that.verifyUrls &&
                notOnPath.equals(that.notOnPath) &&
                onlyOnPath.equals(that.onlyOnPath) &&
                requestHeaders.equals(that.requestHeaders) &&
                fileFilters.equals(that.fileFilters) &&
                downloadFileLocation.equals(that.downloadFileLocation) &&
                stammdatenFilename.equals(that.stammdatenFilename) &&
                startUrl.equals(that.startUrl) &&
                requestHeadersMap.equals(that.requestHeadersMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxLevels, notOnPath, onlyOnPath, requestHeaders, fileFilters, downloadFileLocation, stammdatenFilename, startUrl, requestHeadersMap, verifyUrls);
    }

    @Override
    public String toString() {
        return "CrawlerConfiguration{" +
                "maxLevels=" + maxLevels +
                ", notOnPath='" + notOnPath + '\'' +
                ", onlyOnPath='" + onlyOnPath + '\'' +
                ", requestHeaders='" + requestHeaders + '\'' +
                ", fileFilters='" + fileFilters + '\'' +
                ", downloadFileLocation='" + downloadFileLocation + '\'' +
                ", stammdatenFilename='" + stammdatenFilename + '\'' +
                ", startUrl='" + startUrl + '\'' +
                ", requestHeadersMap=" + requestHeadersMap +
                ", verifyUrls=" + verifyUrls +
                '}';
    }


    public static class Builder {
        private final CrawlerConfiguration configuration = new CrawlerConfiguration();

        public Builder() {}

        public CrawlerConfiguration build() {
            return configuration.copy();
        }

        public Builder setMaxLevels(int maxLevels) {
            configuration.setMaxLevels(maxLevels);
            return this;
        }

        public Builder setNotOnPath(String notOnPath) {
            configuration.setNotOnPath(notOnPath);
            return this;
        }

        public Builder setOnlyOnPath(String onlyOnPath) {
            configuration.setOnlyOnPath(onlyOnPath);
            return this;
        }

        public Builder setStartUrl(String startUrl) {
            configuration.setStartUrl(startUrl);
            return this;
        }

        public Builder setFileFilters(String fileFilters) {
            configuration.setFileFilters(fileFilters);
            return this;
        }

        public Builder setDownloadFileLocation(String downloadFileLocation) {
            configuration.setDownloadFileLocation(downloadFileLocation);
            return this;
        }

        public Builder setVerifyUrls(boolean verifyUrls) {
            configuration.setVerifyUrls(verifyUrls);
            return this;
        }

        public Builder setRequestHeaders(String requestHeaders) {
            configuration.setRequestHeaders(requestHeaders);
            return this;
        }

        public Builder setStammdatenFilename(String stammdatenFilename) {
            configuration.setStammdatenFilename(stammdatenFilename);
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}