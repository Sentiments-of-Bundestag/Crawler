package crawler.core.impl;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.inject.Inject;
import crawler.core.CrawlerURL;
import crawler.core.HTMLPageResponse;
import crawler.core.HTMLPageResponseFetcher;
import crawler.utils.StatusCode;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Fetch urls by a HTTPClient. Note: Will only fetch response headers for resources that fails and
 * for pages (meaning where the body of the response is fetched).
 *
 *
 */
public class HTTPClientResponseFetcher implements HTMLPageResponseFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonClientRequestProcessor.class);
    private final WebClient webClient;

    /**
     * Create a new fetcher.
     *
     * @param client the client to use
     */
    @Inject
    public HTTPClientResponseFetcher(WebClient  client) {
        webClient = client;
    }

    public void shutdown() {
        webClient.close();
    }

    public HTMLPageResponse get(CrawlerURL url, boolean fetchBody, Map<String, String> requestHeaders, boolean followRedirectsToNewDomain) {

        if (url.isWrongSyntax()) {
            return new HTMLPageResponse(url, StatusCode.SC_MALFORMED_URI.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", 0);
        }

        for (String key : requestHeaders.keySet()) {
            webClient.addRequestHeader(key, requestHeaders.get(key));
        }

        final long start = System.currentTimeMillis();

        try {
            HtmlPage resp = webClient.getPage(url.getUrl());

            WebResponse webResp = resp.getWebResponse();
            webClient.waitForBackgroundJavaScriptStartingBefore(2000);
            webClient.waitForBackgroundJavaScript(2000);

            // Get additional ajax hidden slides
            List<HtmlElement> SlideDivs = resp.getBody().getElementsByAttribute("div", "id", "bt-collapse-543410");

            HtmlElement NextSlideButton = null;
            String SlideNavi = null;
            if (SlideDivs.size() > 0) {
                List<HtmlElement> SlideNavis = SlideDivs.get(0).getElementsByAttribute("p", "class", "bt-slider-index");
                if (SlideNavis.size() > 0) {
                    SlideNavi = SlideNavis.get(0).getFirstChild().asText();
                }
                List<HtmlElement> NextSlideButtons = SlideDivs.get(0).getElementsByAttribute("button", "class", "slick-next slick-arrow");
                if (NextSlideButtons.size() > 0) {
                    NextSlideButton = NextSlideButtons.get(0);
                }
            }

            if (NextSlideButton != null && SlideNavi != null) {
                String [] SlideNaviParts = SlideNavi.replace(" ", "").split("/");
                while (SlideNaviParts.length == 2 && Integer.parseInt(SlideNaviParts[0]) < Integer.parseInt(SlideNaviParts[1])) {
                    resp = NextSlideButton.click();
                    webResp = resp.getWebResponse();
                    webClient.waitForBackgroundJavaScriptStartingBefore(2000);
                    webClient.waitForBackgroundJavaScript(2000);

                    // Get additional ajax hidden slides
                    List<HtmlElement> SlideDivsTemp = resp.getBody().getElementsByAttribute("div", "id", "bt-collapse-543410");
                    if (SlideDivsTemp.size() > 0) {
                        List<HtmlElement> SlideNavis = SlideDivs.get(0).getElementsByAttribute("p", "class", "bt-slider-index");
                        if (SlideNavis.size() > 0) {
                            SlideNavi = SlideNavis.get(0).getFirstChild().asText();
                            SlideNaviParts = SlideNavi.replace(" ", "").split("/");
                        }
                        List<HtmlElement> NextSlideButtons = SlideDivs.get(0).getElementsByAttribute("button", "class", "slick-next slick-arrow");
                        if (NextSlideButtons.size() > 0) {
                            NextSlideButton = NextSlideButtons.get(0);
                        }
                    }
                }
            }

            final long fetchTime = System.currentTimeMillis() - start;

            // this is a hack to minimize the amount of memory used
            // should make this configurable maybe
            // don't fetch headers for request that don't fetch the body and
            // response isn't 200
            // these headers will not be shown in the results
            final Map<String, String> headersAndValues =
                    fetchBody || !StatusCode.isResponseCodeOk(webResp.getStatusCode())
                            ? getHeaders(webResp)
                            : Collections.<String, String>emptyMap();

            final String encoding =
                    resp.getXmlEncoding() != null ? resp.getXmlEncoding() : "";

            final String body = fetchBody ? resp.asXml() : "";
            final long size = webResp.getContentLength();
            // TODO add log when null
            final String type =
                    (webResp.getContentType() != null) ? webResp.getContentType() : "";
            final int sc = webResp.getStatusCode();

            return new HTMLPageResponse(url, sc, headersAndValues, body, encoding, size, type, fetchTime);

        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            LOGGER.error(e.getMessage());
            return new HTMLPageResponse(url, StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", System.currentTimeMillis() - start);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new HTMLPageResponse(url, StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
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