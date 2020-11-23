package crawler.core;

import crawler.core.assets.AssetResponse;
import models.Person.Person;
import models.Protokoll;

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
    private final String startPointHost;
    private final Set<AssetResponse> loadedAssets;
    private final Set<Protokoll> loadedProtokolls;
    private final Set<Person> loaderStammdaten;

    /**
     * Create the result from a crawl.
     * @param startPoint where the crawl was started
     * @param urls the urls that was fetched
     * @param verifiedResponses the verified responses
     * @param nonWorkingResponses the non working urls
     * @param startPointHost
     * @param loadedAssets
     * @param loadedProtokolls
     * @param loaderStammdaten
     */
    public CrawlerResult(String startPoint, Set<CrawlerURL> urls,
                         Set<HTMLPageResponse> verifiedResponses,
                         Set<HTMLPageResponse> nonWorkingResponses,
                         String startPointHost, Set<AssetResponse> loadedAssets,
                         Set<Protokoll> loadedProtokolls,
                         Set<Person> loaderStammdaten) {
        this.startPoint = startPoint;
        this.urls = urls;
        this.nonWorkingResponses = nonWorkingResponses;
        this.verifiedResponses = verifiedResponses;
        this.startPointHost = startPointHost;
        this.loadedAssets = loadedAssets;
        this.loadedProtokolls = loadedProtokolls;
        this.loaderStammdaten = loaderStammdaten;
    }

    /**
     * Get the list of downloaded assets
     *
     * @return list of downloaded assets
     */
    public Set<AssetResponse> getLoadedAssets() {return Collections.unmodifiableSet(loadedAssets); }

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
    public String getStartPoint() {
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

    public Set<HTMLPageResponse> getNonWorkingResponses() {
        return nonWorkingResponses;
    }

    public Set<HTMLPageResponse> getVerifiedResponses() {
        return verifiedResponses;
    }

    public String getStartPointHost() {
        return startPointHost;
    }

    public Set<Protokoll> getLoadedProtokolls() {
        return loadedProtokolls;
    }

    public Set<Person> getLoaderStammdaten() {
        return loaderStammdaten;
    }
}