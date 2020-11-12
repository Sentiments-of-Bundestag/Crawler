package crawler.core.assets;


import crawler.core.CrawlerConfiguration;
import crawler.core.HTMLPageResponse;

import java.util.Set;

public interface AssetsVerifier {

    /**
     * Verify that all the assets work (=return 200) for the working urls in the result.
     *
     * @param responses responses to verify
     * @param configuration configuration to verify against
     * @return result of the verification
     */
    AssetsVerificationResult verify(Set<HTMLPageResponse> responses,
                                    CrawlerConfiguration configuration);

    /**
     * Shutdown the crawler and all it's assets.
     */
    void shutdown();

}