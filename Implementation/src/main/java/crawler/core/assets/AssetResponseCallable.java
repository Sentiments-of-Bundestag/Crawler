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
    private final String title;
    private final String referer;
    private final String assetPath;
    public AssetResponseCallable(String theUrl, String theTitle, AssetFetcher theGetter,
                                 Map<String, String> theRequestHeaders, String theReferer, String theAssetPath) {
        url = theUrl;
        title = theTitle;
        getter = theGetter;
        requestHeaders = theRequestHeaders;
        referer = theReferer;
        assetPath = theAssetPath;
    }

    public AssetResponse call() throws InterruptedException {
        return getter.getAsset(new CrawlerURL(url,referer,title), requestHeaders, assetPath);
    }

    @Override
    public String toString() {
        // TODO add request headers
        return this.getClass().getSimpleName() + " url:" + url;
    }
}