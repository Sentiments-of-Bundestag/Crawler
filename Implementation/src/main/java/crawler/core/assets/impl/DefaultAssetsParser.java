package crawler.core.assets.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import crawler.core.CrawlerURL;
import crawler.core.assets.AssetsParser;

import java.util.HashSet;
import java.util.Set;

public class DefaultAssetsParser implements AssetsParser {

    public DefaultAssetsParser() {}

    @Override
    public Set<CrawlerURL> getAssets(Document doc, String referer) {

        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        Set<CrawlerURL> urls = new HashSet<CrawlerURL>(media.size() + imports.size());

        for (Element link : imports) {
            urls.add(new CrawlerURL(link.attr("abs:href"), referer));
        }

        for (Element src : media) {
            urls.add(new CrawlerURL(src.attr("abs:src"), referer));
        }

        return urls;
    }

}