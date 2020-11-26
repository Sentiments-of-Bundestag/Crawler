package crawler.module;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import crawler.core.CrawlerConfiguration;
import crawler.utils.Auth;
import crawler.utils.AuthUtil;
import crawler.utils.HTTPSFaker;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * Provide a HTTPClient.
 *
 *
 */
public class WebClientProvider implements Provider<WebClient> {

    /**
     * The number of threads used in the HTTP Client Manager, meaning we can have this number of HTTP
     * connections open at the same time.
     */
    private final int nrOfThreads;

    /**
     * The number of connections that can be open to the same route. Setting this to the same number
     * as the number of HTTP threads, will ensure that we will use all the thread, even if we only are
     * using one route.
     */
    private final int maxToRoute;

    /**
     * The number in ms before a socket timeout.
     */
    private final int socketTimeout;

    /**
     * The number in ms before a connection timeout.
     */
    private final int connectionTimeout;

    private final Set<Auth> auths;

    private final String proxy;

    private final String filters;

    /**
     * Create a provider.
     *
     * @param maxNrOfThreads the max number of threads in the client
     * @param theSocketTimeout the socket timeout time
     * @param theConnectionTimeout the connection timeout time
     * @param authAsString the auth string
     * @param theProxy the proxy
     * @param theFilters the filters
     */
    @Inject
    public WebClientProvider(
            @Named(CrawlerConfiguration.MAX_THREADS_PROPERTY_NAME) int maxNrOfThreads,
            @Named(CrawlerConfiguration.SOCKET_TIMEOUT_PROPERTY_NAME) int theSocketTimeout,
            @Named(CrawlerConfiguration.CONNECTION_TIMEOUT_PROPERTY_NAME) int theConnectionTimeout,
            @Named(CrawlerConfiguration.AUTH_PROPERTY_NAME) String authAsString,
            @Named(CrawlerConfiguration.PROXY_PROPERTY_NAME) String theProxy,
            @Named(CrawlerConfiguration.FILE_FILTERS_PROPERTY_NAME) String theFilters) {
        nrOfThreads = maxNrOfThreads;
        maxToRoute = maxNrOfThreads;
        connectionTimeout = theConnectionTimeout;
        socketTimeout = theSocketTimeout;
        auths = AuthUtil.getInstance().createAuthsFromString(authAsString);
        proxy = theProxy;
        filters = theFilters;
    }

    /**
     * Get the client.
     *
     * @return the client
     */
    public WebClient get() {
        final WebClient client = HTTPSFaker.getClientThatAllowAnyHTTPS();
        client.getCookieManager().setCookiesEnabled(true);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setTimeout(connectionTimeout);
        client.setCssErrorHandler(new SilentCssErrorHandler());
        //client.getOptions().setRedirectEnabled(true);
        java.util.logging.Logger
                .getLogger("com.gargoylesoftware.htmlunit")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("org.apache.commons.httpclient")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("com.gargoylesoftware.htmlunit.javascript.StrictErrorReporter")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("com.gargoylesoftware.htmlunit.javascript.host.ActiveXObject")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("com.gargoylesoftware.htmlunit.javascript.host.html.HTMLDocument")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("com.gargoylesoftware.htmlunit.html.HtmlScript")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("com.gargoylesoftware.htmlunit.javascript.host.WindowProxy")
                .setLevel(Level.OFF);
        java.util.logging.Logger
                .getLogger("org.apache")
                .setLevel(Level.OFF);
        if (proxy != null && !proxy.isBlank()) {
            StringTokenizer token = new StringTokenizer(proxy, ":");

            if (token.countTokens() == 3) {
                String proxyHost = token.nextToken();
                int proxyPort = Integer.parseInt(token.nextToken());

                client.getOptions().setProxyConfig(new ProxyConfig(proxyHost, proxyPort));
            } else
                System.err.println("Invalid proxy configuration: " + proxy);
        }

        if (auths.size() > 0) {

            for (Auth authObject : auths) {
                client.getCredentialsProvider().setCredentials(
                        new AuthScope(authObject.getScope(), authObject.getPort()),
                        new UsernamePasswordCredentials(authObject.getUserName(), authObject.getPassword()));
            }
        }

        return client;
    }
}
