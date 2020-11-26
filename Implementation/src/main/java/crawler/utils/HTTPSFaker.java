package crawler.utils;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * This class provides an instance of HttpClient that accepts Https Certificates
 */
public final class HTTPSFaker {
    private static final int HTTPS_PORT = 443;
    private static final String HTTPS = "https";

    private HTTPSFaker() {}

    /**
     * Get a HttpClient that accept any HTTP certificate.
     *
     * @return a httpClient that accept any HTTP certificate
     */
    public static WebClient getClientThatAllowAnyHTTPS() {

        WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
        webClient.getOptions().setUseInsecureSSL(true); //ignore ssl certificate
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        return webClient;
    }
}
