package crawler.core;

import java.util.Collections;
import java.util.Set;


/**
 * The result of a crawl.
 *
 */
public class CrawlerResult {

    private final Set<CrawlerURL> urls;
    private final Set<HTMLPageResponse> nonWorkingResponses;
    private final Set<HTMLPageResponse> verifiedResponses;
    private final String startPoint;

    /**
     * Create the result from a crawl.
     *
     * @param theStartPoint where the crawl was started
     * @param theUrls the urls that was fetched
     * @param theVerifiedResponses the verified responses
     * @param theNonWorkingResponses the non working urls
     */
    public CrawlerResult(String theStartPoint, Set<CrawlerURL> theUrls,
                         Set<HTMLPageResponse> theVerifiedResponses, Set<HTMLPageResponse> theNonWorkingResponses) {
        startPoint = theStartPoint;
        urls = theUrls;
        nonWorkingResponses = theNonWorkingResponses;
        verifiedResponses = theVerifiedResponses;
    }

    /**
     * Get the non working urls.
     *
     * @return non working urls.
     */
    public Set<HTMLPageResponse> getNonWorkingUrls() {
        return Collections.unmodifiableSet(nonWorkingResponses);
    }

    /**
     * Get verified working responses. Can be empty is verification is turned off.
     *
     * @return non working urls.
     */
    public Set<HTMLPageResponse> getVerifiedURLResponses() {
        return Collections.unmodifiableSet(verifiedResponses);
    }

    /**
     * Get the start point of the crawl.
     *
     * @return the start point of the crawl.
     */
    public String getTheStartPoint() {
        return startPoint;
    }

    /**
     * Get the fetched urls.
     *
     * @return the fetched urls. Contains only working url if verification is turned on.
     */
    public Set<CrawlerURL> getUrls() {
        return Collections.unmodifiableSet(urls);
    }

}