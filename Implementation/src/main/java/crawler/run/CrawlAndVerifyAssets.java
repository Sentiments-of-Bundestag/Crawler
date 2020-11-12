package crawler.run;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.cli.ParseException;
import crawler.core.Crawler;
import crawler.core.CrawlerResult;
import crawler.core.assets.AssetResponse;
import crawler.core.assets.AssetsVerificationResult;
import crawler.core.assets.AssetsVerifier;
import crawler.module.CrawlModule;
import crawler.util.StatusCode;


public class CrawlAndVerifyAssets extends AbstractCrawl {

    CrawlAndVerifyAssets(String[] args) throws ParseException {
        super(args);

    }

    /**
     * Run.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        try {
            final CrawlAndVerifyAssets crawl = new CrawlAndVerifyAssets(args);
            crawl.crawl();

        } catch (ParseException e) {
            System.err.print(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }

    private void crawl() {
        final Injector injector = Guice.createInjector(new CrawlModule());
        final Crawler crawler = injector.getInstance(Crawler.class);

        System.out.println("Start crawling ...");
        final CrawlerResult result = crawler.getUrls(getConfiguration());
        System.out.println("Crawled  " + result.getVerifiedURLResponses().size() + " pages");

        System.out.println("Start verify assets ...");
        AssetsVerifier verifier = injector.getInstance(AssetsVerifier.class);
        AssetsVerificationResult assetsResult =
                verifier.verify(result.getVerifiedURLResponses(), getConfiguration());

        System.out.println(assetsResult.getWorkingAssets().size() + " assets is ok, "
                + assetsResult.getNonWorkingAssets().size() + " is not");

        for (AssetResponse resp : assetsResult.getNonWorkingAssets()) {
            System.out.println(resp.getUrl() + " code:"
                    + StatusCode.toFriendlyName(resp.getResponseCode()) + " from URL:" + resp.getReferer());
        }

        crawler.shutdown();
        verifier.shutdown();
    }
}