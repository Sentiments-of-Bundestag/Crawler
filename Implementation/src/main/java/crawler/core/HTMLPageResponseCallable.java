package crawler.core;

import java.util.Map;
import java.util.concurrent.Callable;



/**
 * A callable that fetch a HTTP response code and return response to the caller.
 *
 */
public class HTMLPageResponseCallable implements Callable<HTMLPageResponse> {

    private final HTMLPageResponseFetcher fetcher;
    private final CrawlerURL url;
    private final boolean fetchPage;
    private final boolean followRedirectsToNewDomain;
    private final Map<String, String> requestHeaders;


    /**
     * Create a new callable.
     *
     * @param theUrl the url to call.
     * @param theFetcher the fetcher to use
     * @param fetchTheBody if true, the response body is fetched, else not.
     * @param theRequestHeaders request headers to add
     * @param followRedirectsToNewDomain if true, follow redirects that lead to a different domain.
     */
    public HTMLPageResponseCallable(CrawlerURL theUrl, HTMLPageResponseFetcher theFetcher,
                                    boolean fetchTheBody, Map<String, String> theRequestHeaders, boolean followRedirectsToNewDomain) {

        url = theUrl;
        fetcher = theFetcher;
        fetchPage = fetchTheBody;
        requestHeaders = theRequestHeaders;
        this.followRedirectsToNewDomain = followRedirectsToNewDomain;
    }

    /**
     * Fetch the actual response.
     *
     * @return the response
     * @throws InterruptedException if it takes longer time than the configured max time to fetch the
     *         response
     */
    public HTMLPageResponse call() throws InterruptedException {
        return fetcher.get(url, fetchPage, requestHeaders, followRedirectsToNewDomain);
    }

    @Override
    public String toString() {
        return "HTMLPageResponseCallable{" +
                "fetcher=" + fetcher +
                ", url=" + url +
                ", fetchPage=" + fetchPage +
                ", followRedirectsToNewDomain=" + followRedirectsToNewDomain +
                ", requestHeaders=" + requestHeaders +
                '}';
    }
}