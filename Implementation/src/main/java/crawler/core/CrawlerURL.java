package crawler.core;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;


/**
 * A page url.
 *
 */
public class CrawlerURL {

    private final String url;
    private final URI uri;
    private final String host;
    private final String referer;
    private final boolean isWrongSyntax;

    /**
     * Create a page url with a blank referer.
     *
     * @param theUrl to the asset
     */
    public CrawlerURL(String theUrl) {
        this(theUrl, "");
    }

    /**
     * Create a page url with an referer.
     *
     * @param theUrl to the asset.
     * @param theUrlReferer the url to the referer.
     */
    public CrawlerURL(String theUrl, String theUrlReferer) {
        url = theUrl;
        referer = theUrlReferer;
        URI tmpURI = null;
        try {
            URL u = null;

            // sometimes the urls are encoded
            // but how do we handle the problem with url:s that are
            // encoded but not the + sign?
            // better to check if it contains faulty characters
            // if (url.matches("@^[a-zA-Z0-9%+-_]*$@"))
            if (url.contains("%"))
                u = new URL(URLDecoder.decode(theUrl, "UTF-8"));
            else
                u = new URL(theUrl);

            // skipping the segment part, since the # is only for the browser
            tmpURI =
                    new URI(u.getProtocol(), u.getUserInfo(), u.getHost(), u.getPort(), u.getPath(),
                            u.getQuery(), null);

        } catch (Exception e) {
            // an ugly catch all, we should act on it somehow
        }
        uri = tmpURI;
        isWrongSyntax = (uri == null);
        host = (uri == null) ? null : uri.getHost();
    }

    public boolean isWrongSyntax() {
        return isWrongSyntax;
    }

    public URI getUri() {
        return uri;
    }

    public String getHost() {
        return host;
    }

    public String getReferer() {
        return referer;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " url:" + url;
    }

    @Override
    public int hashCode() {
        // here's a hack for saying http://example.com is the same as http://example.com/
        if (uri == null) return 0;
        final int prime = 31;
        int result = 1;
        String hash = uri.toString();
        if (hash.endsWith("/")) hash = hash.substring(0, hash.length() - 1);
        result = prime * result + hash.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CrawlerURL other = (CrawlerURL) obj;
        if (uri == null) {
            if (other.uri != null) return false;
        } else if (uri.equals(other.uri))
            return true;
            // here's a hack for saying http://example.com is the same as
            // http://example.com/
        else if (uri.toString().endsWith("/")) {
            String withoutEndingSlash = uri.toString().substring(0, uri.toString().length() - 1);
            if (withoutEndingSlash.equals(other.uri.toString())) return true;
        }

        return false;
    }

}