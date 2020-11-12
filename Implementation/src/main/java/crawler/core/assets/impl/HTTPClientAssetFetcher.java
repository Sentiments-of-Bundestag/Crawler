package crawler.core.assets.impl;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.inject.Inject;
import crawler.core.CrawlerURL;
import crawler.core.assets.AssetFetcher;
import crawler.core.assets.AssetResponse;
import crawler.util.StatusCode;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

public class HTTPClientAssetFetcher implements AssetFetcher {

    private final WebClient httpClient;

    @Inject
    public HTTPClientAssetFetcher(WebClient client) {
        httpClient = client;
    }

    /**
     * Shutdown the client.
     */
    public void shutdown() {
        httpClient.close();
    }

    @Override
    public AssetResponse getAsset(CrawlerURL url, Map<String, String> requestHeaders) {
        for (String key : requestHeaders.keySet()) {
            httpClient.addRequestHeader(key, requestHeaders.get(key));
        }

        final long start = System.currentTimeMillis();
        try {

            final HtmlPage resp = httpClient.getPage(url.getUrl());
            WebResponse webResp = resp.getWebResponse();
            final long time = System.currentTimeMillis() - start;
            final int sc = webResp.getStatusCode();

            return new AssetResponse(url.getUrl(), url.getReferer(), sc, time);

        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            return new AssetResponse(url.getUrl(), url.getReferer(), StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
                    System.currentTimeMillis() - start);
        } catch (IOException e) {
            e.printStackTrace();
            return new AssetResponse(url.getUrl(), url.getReferer(), StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), -1);
        } finally {

            httpClient.close();
        }
    }
}