package crawler.core;

import com.gargoylesoftware.htmlunit.HttpMethod;

import java.util.Map;
import java.util.concurrent.Callable;

public class JsonRequestCallable implements Callable<HTMLPageResponse> {
    private final JsonRequestProcessor processor;
    private final CrawlerURL url;
    private final HttpMethod httpMethod;
    private final boolean fetchBody;
    private final Map<String, String> requestHeaders;
    private final String requestBody;


    /**
     *
     * @param processor the processor to use
     * @param url the url to call.
     * @param httpMethod the http method for the request
     * @param fetchBody if true, the response body is fetched, else not.
     * @param requestHeaders request headers to add
     * @param requestBody the http request body string
     */
    public JsonRequestCallable(JsonRequestProcessor processor, CrawlerURL url, HttpMethod httpMethod, boolean fetchBody, Map<String, String> requestHeaders, String requestBody) {
        this.processor = processor;
        this.url = url;
        this.httpMethod = httpMethod;
        this.fetchBody = fetchBody;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }


    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws InterruptedException if it takes longer time than the configured max time to fetch the
     *      response
     */
    @Override
    public HTMLPageResponse call() throws InterruptedException {
        return processor.get(url, httpMethod, fetchBody, requestHeaders, requestBody);
    }

    @Override
    public String toString() {
        return "JsonRequestCallable{" +
                "processor=" + processor +
                ", url=" + url +
                ", httpMethod=" + httpMethod +
                ", fetchBody=" + fetchBody +
                ", requestHeaders=" + requestHeaders +
                ", requestBody='" + requestBody + '\'' +
                '}';
    }
}