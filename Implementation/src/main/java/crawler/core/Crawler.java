package crawler.core;

import models.Crawler.Url;
import models.Person.Person;

import java.util.Map;
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
     * @param dbStammdaten list of persons in the db
     * @return the result of the crawl
     */
    CrawlerResult getUrls(CrawlerConfiguration configuration, Set<Url> dbUrls, Set<Person> dbStammdaten, boolean deleteAfterParsing);

    /**
     * Send notifications about new protokolls
     *
     * @param notificationString notification with list of protokoll ids
     * @return the confirmation of notification request
     */
    HTMLPageResponse sendNotification(CrawlerURL crawlerURL, String notificationString, Map<String, String> requestHeaders);

    /**
     * Shutdown the crawler and all it's assets.
     */
    void shutdown();

}