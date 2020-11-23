package crawler.module;

import com.gargoylesoftware.htmlunit.WebClient;
import crawler.core.Crawler;
import crawler.core.HTMLPageResponseFetcher;
import crawler.core.JsonRequestProcessor;
import crawler.core.PageURLParser;
import crawler.core.assets.AssetFetcher;
import crawler.core.assets.AssetsParser;
import crawler.core.assets.impl.DefaultAssetsParser;
import crawler.core.assets.impl.HTTPClientAssetFetcher;
import crawler.core.impl.AhrefPageURLParser;
import crawler.core.impl.DefaultCrawler;
import crawler.core.impl.HTTPClientResponseFetcher;
import crawler.core.impl.JsonClientRequestProcessor;

import java.util.concurrent.ExecutorService;

/**
 * Module for a crawl.
 *
 */
public class CrawlModule extends AbstractPropertiesModule {


    /**
     * Bind the classes.
     */
    @Override
    protected void configure() {
        super.configure();
        bind(Crawler.class).to(DefaultCrawler.class);
        bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class);
        bind(HTMLPageResponseFetcher.class).to(HTTPClientResponseFetcher.class);
        bind(JsonRequestProcessor.class).to(JsonClientRequestProcessor.class);
        bind(WebClient.class).toProvider(WebClientProvider.class);
        bind(PageURLParser.class).to(AhrefPageURLParser.class);

        // For parsing assets
        bind(AssetsParser.class).to(DefaultAssetsParser.class);
        bind(AssetFetcher.class).to(HTTPClientAssetFetcher.class);
    }
}