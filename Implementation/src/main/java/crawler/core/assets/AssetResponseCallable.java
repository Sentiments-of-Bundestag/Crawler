package crawler.core.assets;

import crawler.core.CrawlerURL;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * A callable that fetch a HTTP response code and return response to the caller.
 */
public class AssetResponseCallable implements Callable<AssetResponse> {

    private final AssetFetcher getter;
    private final Map<String, String> requestHeaders;
    private final String url;
    private final String referer;
    public AssetResponseCallable(String theUrl, AssetFetcher theGetter,
                                 Map<String, String> theRequestHeaders, String theReferer) {
        url = theUrl;
        getter = theGetter;
        requestHeaders = theRequestHeaders;
        referer = theReferer;
    }

    public AssetResponse call() throws InterruptedException {
        return getter.getAsset(new CrawlerURL(url,referer), requestHeaders);
    }

    @Override
    public String toString() {
        // TODO add request headers
        return this.getClass().getSimpleName() + " url:" + url;
    }
}