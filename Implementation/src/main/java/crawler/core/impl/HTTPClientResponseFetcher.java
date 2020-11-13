package crawler.core.impl;


import com.gargoylesoftware.htmlunit.HttpHeader;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.inject.Inject;
import crawler.core.CrawlerURL;
import crawler.core.HTMLPageResponse;
import crawler.core.HTMLPageResponseFetcher;
import crawler.util.StatusCode;
import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetch urls by a HTTPClient. Note: Will only fetch response headers for resources that fails and
 * for pages (meaning where the body of the response is fetched).
 *
 *
 */
public class HTTPClientResponseFetcher implements HTMLPageResponseFetcher {

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

    public HTMLPageResponse get(CrawlerURL url, boolean getPage, Map<String, String> requestHeaders, boolean followRedirectsToNewDomain) {

        if (url.isWrongSyntax()) {
            return new HTMLPageResponse(url, StatusCode.SC_MALFORMED_URI.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", 0);
        }

        for (String key : requestHeaders.keySet()) {
            webClient.addRequestHeader(key, requestHeaders.get(key));
        }

        final long start = System.currentTimeMillis();

        try {
            final HtmlPage resp = webClient.getPage(url.getUrl());

            WebResponse webResp = resp.getWebResponse();
            webClient.waitForBackgroundJavaScriptStartingBefore(200);
            webClient.waitForBackgroundJavaScript(2000);

            final long fetchTime = System.currentTimeMillis() - start;

            // this is a hack to minimize the amount of memory used
            // should make this configurable maybe
            // don't fetch headers for request that don't fetch the body and
            // response isn't 200
            // these headers will not be shown in the results
            final Map<String, String> headersAndValues =
                    getPage || !StatusCode.isResponseCodeOk(webResp.getStatusCode())
                            ? getHeaders(webResp)
                            : Collections.<String, String>emptyMap();

            final String encoding =
                    resp.getXmlEncoding() != null ? resp.getXmlEncoding() : "";

            final String body = getPage ? resp.asXml() : "";
            final long size = webResp.getContentLength();
            // TODO add log when null
            final String type =
                    (webResp.getContentType() != null) ? webResp.getContentType() : "";
            final int sc = webResp.getStatusCode();

            return new HTMLPageResponse(url, sc, headersAndValues, body, encoding, size, type, fetchTime);

        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            System.err.println(e);
            return new HTMLPageResponse(url, StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", System.currentTimeMillis() - start);
        } catch (IOException e) {
            System.err.println(e);
            return new HTMLPageResponse(url, StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", -1);
        } finally {
            webClient.close();
        }
    }

    public HTMLPageResponse saveAs(final File file, CrawlerURL crawlerURL, boolean getPage, Map<String, String> requestHeaders) {

        for (String key : requestHeaders.keySet()) {
            webClient.addRequestHeader(key, requestHeaders.get(key));
        }

        final long start = System.currentTimeMillis();

        try {
            final HtmlPage page = webClient.getPage(crawlerURL.getUrl());
            final URL url = page.getFullyQualifiedUrl(crawlerURL.getUrl());
            final WebRequest request = new WebRequest(url);
            request.setCharset(page.getCharset());
            request.setAdditionalHeader(HttpHeader.REFERER, page.getUrl().toExternalForm());
            final WebResponse webResponse = webClient.loadWebResponse(request);
            try (OutputStream fos = Files.newOutputStream(file.toPath());
                 InputStream content =  webResponse.getContentAsStream()) {
                IOUtils.copy(content, fos);
            }

            final long fetchTime = System.currentTimeMillis() - start;

            // this is a hack to minimize the amount of memory used
            // should make this configurable maybe
            // don't fetch headers for request that don't fetch the body and
            // response isn't 200
            // these headers will not be shown in the results
            final Map<String, String> headersAndValues =
                    getPage || !StatusCode.isResponseCodeOk(webResponse.getStatusCode())
                            ? getHeaders(webResponse)
                            : Collections.<String, String>emptyMap();

            final String encoding =
                    page.getXmlEncoding() != null ? page.getXmlEncoding() : "";

            final String body = getPage ? page.asXml() : "";
            final long size = webResponse.getContentLength();
            // TODO add log when null
            final String type =
                    (webResponse.getContentType() != null) ? webResponse.getContentType() : "";
            final int sc = webResponse.getStatusCode();

            return new HTMLPageResponse(crawlerURL, sc, headersAndValues, body, encoding, size, type, fetchTime);
        } catch (IOException e) {
            System.err.println(e);
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