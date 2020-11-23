package crawler.core;

import java.util.Map;

/**
 * Interface for the response fetchers.
 *
 */
public interface HTMLPageResponseFetcher {

    /**
     * Get the response for this url, never fetch the body.
     *
     * @param url the url to fetch
     * @param fetchBody fetch the body or not
     * @param requestHeaders request headers to add
     * @param followRedirectsToNewDomain if true, follow redirects that lead to a different domain.
     * @return the response
     */
    HTMLPageResponse get(CrawlerURL url, boolean fetchBody,
                         Map<String, String> requestHeaders,
                         boolean followRedirectsToNewDomain);

    /**
     * Shutdown the fetcher and all of it's assets.
     */
    void shutdown();
}
