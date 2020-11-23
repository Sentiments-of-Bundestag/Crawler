package crawler.core.impl;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.inject.Inject;
import crawler.core.CrawlerURL;
import crawler.core.HTMLPageResponse;
import crawler.core.JsonRequestProcessor;
import crawler.util.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonClientRequestProcessor implements JsonRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonClientRequestProcessor.class);
    private final WebClient webClient;

    /**
     * Create a new processor.
     *
     * @param client the client to use
     */
    @Inject
    public JsonClientRequestProcessor(WebClient  client) {
        webClient = client;
    }

    /**
     * Shutdown the processor and all of it's assets.
     */
    @Override
    public void shutdown() {
        webClient.close();
    }

    /**
     * Get the response for this url, never fetch the body.
     *
     * @param crawlerURL            the url to be requested
     * @param httpMethod     method of request (get, post, delete, update, etc.)
     * @param fetchBody      fetch the body or not
     * @param requestHeaders request headers to add
     * @param requestBody    request body parameter (json).
     * @return the response
     */
    @Override
    public HTMLPageResponse get(CrawlerURL crawlerURL, HttpMethod httpMethod, boolean fetchBody, Map<String, String> requestHeaders, String requestBody) {

        if (crawlerURL.isWrongSyntax()) {
            return new HTMLPageResponse(crawlerURL, StatusCode.SC_MALFORMED_URI.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", 0);
        }

        try {
            URL url = new URL(crawlerURL.getUrl());
            WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);

            for (String key : requestHeaders.keySet()) {
                requestSettings.setAdditionalHeader(key, requestHeaders.get(key));
            }

            final long start = System.currentTimeMillis();

            final Page resp = webClient.getPage(requestSettings);
            WebResponse webResp = resp.getWebResponse();
            final long fetchTime = System.currentTimeMillis() - start;

            final Map<String, String> headersAndValues =
                    fetchBody || !StatusCode.isResponseCodeOk(webResp.getStatusCode())
                            ? getHeaders(webResp)
                            : Collections.<String, String>emptyMap();

            final String body = fetchBody ? webResp.getContentAsString() : "";
            final long size = webResp.getContentLength();
            // TODO add log when null
            final String type =
                    (webResp.getContentType() != null) ? webResp.getContentType() : "";
            final int sc = webResp.getStatusCode();

            return new HTMLPageResponse(crawlerURL, sc, headersAndValues, body, "", size, type, fetchTime);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new HTMLPageResponse(crawlerURL, StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", -1);
        } finally {
            webClient.close();
        }
    }

    /**
     * Get the headers from the response.
     *
     * @param webResp the response
     * @return the headers as a key/value map.
     */
    protected Map<String, String> getHeaders(WebResponse webResp) {
        final Map<String, String> headersAndValues = new HashMap<String, String>();

        List<NameValuePair> httpHeaders =  webResp.getResponseHeaders();
        for (NameValuePair header : httpHeaders) {
            headersAndValues.put(header.getName(), header.getValue());
        }
        return headersAndValues;
    }
}
