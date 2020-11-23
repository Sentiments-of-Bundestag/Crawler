package crawler.core;

import com.gargoylesoftware.htmlunit.HttpMethod;

import java.util.Map;

public interface JsonRequestProcessor {

    /**
     * Get the response for this url, never fetch the body.
     *
     * @param url the url to be requested
     * @param httpMethod method of request (get, post, delete, update, etc.)
     * @param fetchBody fetch the body or not
     * @param requestHeaders request headers to add
     * @param requestBody request body parameter (json).
     * @return the response
     */
    HTMLPageResponse get(CrawlerURL url, HttpMethod httpMethod, boolean fetchBody,
                         Map<String, String> requestHeaders, String requestBody);

    /**
     * Shutdown the fetcher and all of it's assets.
     */
    void shutdown();
}
