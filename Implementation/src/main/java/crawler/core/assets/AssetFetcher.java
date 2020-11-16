package crawler.core.assets;

import crawler.core.CrawlerURL;

import java.util.Map;

/**
 * Interface of a asset fetcher.
 *
 */
public interface AssetFetcher {

    /**
     * Fetch the asset.
     *
     * @param url the url of the assets.
     * @param requestHeaders request headers for the fetch.
     * @return the asset response
     */
    AssetResponse getAsset(CrawlerURL url, Map<String, String> requestHeaders);

    /**
     * Shutdown the fetcher and all of it's assets.
     */
    void shutdown();

}