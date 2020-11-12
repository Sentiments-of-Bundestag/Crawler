package crawler.core;

/**
 * Interface of a web crawler.
 *
 */
public interface Crawler {
    /**
     * Get the urls.
     *
     * @param configuration how to perform the crawl
     * @return the result of the crawl
     */
    CrawlerResult getUrls(CrawlerConfiguration configuration);

    /**
     * Shutdown the crawler and all it's assets.
     */
    void shutdown();

}