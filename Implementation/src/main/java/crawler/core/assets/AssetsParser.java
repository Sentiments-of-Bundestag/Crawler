package crawler.core.assets;

import org.jsoup.nodes.Document;
import crawler.core.CrawlerURL;

import java.util.Set;

/**
 * Interface for parsing all the assets of a HTML document.
 *
 */
public interface AssetsParser {

    Set<CrawlerURL> getAssets(Document doc, String referer);
}