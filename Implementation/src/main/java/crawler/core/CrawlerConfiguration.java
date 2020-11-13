package crawler.core;

import crawler.util.HeaderUtil;

import java.util.Collections;
import java.util.Map;

/**
 * Configuration for a crawl.
 *
 */
public final class CrawlerConfiguration {

    // Property names for System properties.

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

    private String startUrl;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxLevels;
        result = prime * result + ((notOnPath == null) ? 0 : notOnPath.hashCode());
        result = prime * result + ((onlyOnPath == null) ? 0 : onlyOnPath.hashCode());
        result = prime * result + ((startUrl == null) ? 0 : startUrl.hashCode());
        result = prime * result + ((fileFilters == null) ? 0 : fileFilters.hashCode());
        result = prime * result + ((downloadFileLocation == null) ? 0 : downloadFileLocation.hashCode());
        result = prime * result + (verifyUrls ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CrawlerConfiguration other = (CrawlerConfiguration) obj;
        if (maxLevels != other.maxLevels) return false;
        if (notOnPath == null) {
            if (other.notOnPath != null) return false;
        } else if (!notOnPath.equals(other.notOnPath)) return false;
        if (onlyOnPath == null) {
            if (other.onlyOnPath != null) return false;
        } else if (!onlyOnPath.equals(other.onlyOnPath)) return false;
        if (startUrl == null) {
            if (other.startUrl != null) return false;
        } else if (!startUrl.equals(other.startUrl)) return false;
        if (fileFilters != other.fileFilters) return false;
        if (downloadFileLocation != other.downloadFileLocation) return false;
        if (verifyUrls != other.verifyUrls) return false;
        return true;
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
    }

    public static Builder builder() {
        return new Builder();
    }
}