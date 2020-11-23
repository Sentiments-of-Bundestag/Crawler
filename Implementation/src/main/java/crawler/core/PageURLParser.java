package crawler.core;

import java.util.Set;


/**
 * Fetch all a href:s within a response.
 *
 */
public interface PageURLParser {

    /**
     * Get the a links, parsed by this parser.
     *
     * @param theResponse to parse
     * @return a set of urls
     */
    Set<CrawlerURL> get(HTMLPageResponse theResponse);

}