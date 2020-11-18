package crawler.core;

import models.Crawler.Url;

import java.util.Set;

/**
 * Interface of a web crawler.
 *
 */
public interface Crawler {
    /**
     * Get the urls.
     *
     * @param configuration how to perform the crawl
     * @param dbUrls list of urls from the db
     * @return the result of the crawl
     */
    CrawlerResult getUrls(CrawlerConfiguration configuration, Set<Url> dbUrls);

    /**
     * Shutdown the crawler and all it's assets.
     */
    void shutdown();

}