package crawler.core.impl;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import crawler.core.*;
import crawler.core.assets.AssetFetcher;
import crawler.core.assets.AssetResponse;
import crawler.core.assets.AssetResponseCallable;
import crawler.core.assets.AssetsParser;
import crawler.utils.StatusCode;
import models.Crawler.Url;
import models.Person.Person;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.service.DynamicScheduler;

import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Crawl urls within the same domain.
 *
 */
public class DefaultCrawler implements Crawler {

    private final String fileFilters;
    private final String downloadFileLocation;
    private final String stammdatenFilename;
    private final HTMLPageResponseFetcher responseFetcher;
    private final AssetFetcher assetFetcher;
    private final ExecutorService service;
    private final PageURLParser parser;
    private final AssetsParser assetsParser;
    private final JsonRequestProcessor requestProcessor;
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicScheduler.class);

    /**
     * Create a new crawler.
     * @param responseFetcher the response fetcher to use.
     * @param service the thread pool.
     * @param parser the parser.
     * @param assetFetcher the asset fetcher to use
     * @param assetsParser the asset parser to use
     * @param requestProcessor the json request processor to use
     * @param stammdatenFilename the filename of the default stammdaten file
     */
    @Inject
    public DefaultCrawler(HTMLPageResponseFetcher responseFetcher, ExecutorService service,
                          PageURLParser parser,
                          AssetFetcher assetFetcher,
                          AssetsParser assetsParser,
                          JsonRequestProcessor requestProcessor,
                          @Named(CrawlerConfiguration.FILE_FILTERS_PROPERTY_NAME) String fileFilters,
                          @Named(CrawlerConfiguration.DEFAULT_DOWNLOAD_FILE_LOCATION_PROPERTY_NAME) String downloadFileLocation,
                          @Named(CrawlerConfiguration.DEFAULT_STAMMDATEN_FILENAME) String stammdatenFilename) {
        this.service = service;
        this.responseFetcher = responseFetcher;
        this.parser = parser;
        this.fileFilters = fileFilters;
        this.downloadFileLocation = downloadFileLocation;
        this.assetFetcher = assetFetcher;
        this.assetsParser = assetsParser;
        this.requestProcessor = requestProcessor;
        this.stammdatenFilename = stammdatenFilename;
    }

    /**
     * Shutdown the crawler.
     */
    public void shutdown() {
        if (service != null) service.shutdown();
        if (responseFetcher != null) responseFetcher.shutdown();
        if (assetFetcher != null) assetFetcher.shutdown();
        if (requestProcessor != null) requestProcessor.shutdown();
    }

    /**
     * Get the urls.
     *
     * @param configuration how to perform the crawl
     * @param dbUrls list of urls from the db
     * @return the result of the crawl
     */
    public CrawlerResult getUrls(CrawlerConfiguration configuration, Set<Url> dbUrls, Set<Person> dbStammdaten, boolean deleteAfterParsing) {
        final Map<String, String> requestHeaders = configuration.getRequestHeadersMap();
        final HTMLPageResponse resp =
                verifyInput(configuration.getStartUrl(), configuration.getOnlyOnPath(), requestHeaders);

        int level = 0;
        Set<Person> loaderStammdaten = new LinkedHashSet<>();
        final Set<CrawlerURL> allUrls = new LinkedHashSet<CrawlerURL>();
        final Set<HTMLPageResponse> verifiedUrls = new LinkedHashSet<HTMLPageResponse>();
        final Set<HTMLPageResponse> nonWorkingResponses = new LinkedHashSet<HTMLPageResponse>();
        Set<AssetResponse> loadedAssets = new LinkedHashSet<AssetResponse>();

        verifiedUrls.add(resp);

        final String host = resp.getPageUrl().getHost();

        if (configuration.getMaxLevels() > 0) {

            // set the start url
            Set<CrawlerURL> nextToFetch = new LinkedHashSet<CrawlerURL>();
            nextToFetch.add(resp.getPageUrl());

            while (level < configuration.getMaxLevels()) {

                final Map<Future<HTMLPageResponse>, CrawlerURL> futures =
                        new HashMap<Future<HTMLPageResponse>, CrawlerURL>(nextToFetch.size());

                for (CrawlerURL testURL : nextToFetch) {
                    futures.put(service.submit(new HTMLPageResponseCallable(testURL, responseFetcher, true,
                            requestHeaders, false)), testURL);
                }

                nextToFetch =
                        fetchNextLevelLinks(futures, allUrls, nonWorkingResponses, verifiedUrls, host,
                                configuration.getOnlyOnPath(), configuration.getNotOnPath());
                level++;
            }
        } else {
            allUrls.add(resp.getPageUrl());
        }

        if (configuration.isVerifyUrls()) {
            loadedAssets = verifyUrlsAndLoadFiles(allUrls, verifiedUrls, nonWorkingResponses, requestHeaders, dbUrls);
        }
        //verifyUrls(allUrls, verifiedUrls, nonWorkingResponses, requestHeaders);

        LinkedHashSet<CrawlerURL> workingUrls = new LinkedHashSet<CrawlerURL>();
        for (HTMLPageResponse workingResponses : verifiedUrls) {
            workingUrls.add(workingResponses.getPageUrl());
        }

        // TODO find a better fix for this
        // wow, this is a hack to fix if the first URL is redirected,
        // then we want to keep that original start url
        if (workingUrls.size() >= 1) {
            List<CrawlerURL> list = new ArrayList<CrawlerURL>(workingUrls);
            list.add(0, new CrawlerURL(configuration.getStartUrl()));
            list.remove(1);
            workingUrls.clear();
            workingUrls.addAll(list);
        }

        if (dbStammdaten != null && dbStammdaten.size() > 0)
        {
            loaderStammdaten = dbStammdaten;
        } else {
            // Search for stammdatenFilename in the list of loadedAssets
            AssetResponse stammdatenAsset = null;

            for(AssetResponse assetResponse : loadedAssets) {
                if (assetResponse.getUrl() != null && assetResponse.getUrl().toLowerCase().contains(stammdatenFilename.toLowerCase()) &&
                        assetResponse.getAssetPath() != null && assetResponse.getAssetPath().toLowerCase().contains(stammdatenFilename.toLowerCase())) {
                    stammdatenAsset = assetResponse;
                    break;
                }
            }

            if (stammdatenAsset != null) {
                loaderStammdaten = assetsParser.getStammdaten(stammdatenAsset, deleteAfterParsing);
                loadedAssets.remove(stammdatenAsset);
            }
        }

        return new CrawlerResult(configuration.getStartUrl(), configuration.isVerifyUrls()
                ? workingUrls
                : allUrls, verifiedUrls, nonWorkingResponses, host, loadedAssets, assetsParser.getProtokolls(loadedAssets, loaderStammdaten, deleteAfterParsing), loaderStammdaten);
    }

    /**
     * Send notifications about new protokolls
     *
     * @param notificationString notification with list of protokoll ids
     * @return the confirmation of notification request
     */
    public HTMLPageResponse sendNotification(CrawlerURL crawlerURL, String notificationString, Map<String, String> requestHeaders) {

        final Callable<HTMLPageResponse> task = new JsonRequestCallable(requestProcessor, crawlerURL, HttpMethod.POST, true, requestHeaders, notificationString);

        Future<HTMLPageResponse> future = service.submit(task);

        try {
            if (!future.isCancelled()) {
                return future.get();
            } else {
                return new HTMLPageResponse(crawlerURL,
                        StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
                        Collections.<String, String>emptyMap(), "", "", 0, "", 0);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new HTMLPageResponse(crawlerURL,
                    StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
                    Collections.<String, String>emptyMap(), "", "", 0, "", -1);
        }
    }

    /**
     * Fetch links to the next level of the crawl.
     *
     * @param responses holding bodys where we should fetch the links.
     * @param allUrls every url we have fetched so far
     * @param nonWorkingUrls the urls that didn't work to fetch
     * @param verifiedUrls responses that are already verified
     * @param host the host we are working on
     * @param onlyOnPath only fetch files that match the following path. If empty, all will match.
     * @param notOnPath don't collect/follow urls that contains this text in the url
     * @return the next level of links that we should fetch
     */
    protected Set<CrawlerURL> fetchNextLevelLinks(Map<Future<HTMLPageResponse>, CrawlerURL> responses,
                                                  Set<CrawlerURL> allUrls, Set<HTMLPageResponse> nonWorkingUrls,
                                                  Set<HTMLPageResponse> verifiedUrls, String host, String onlyOnPath, String notOnPath) {

        final Set<CrawlerURL> nextLevel = new LinkedHashSet<CrawlerURL>();

        final Iterator<Entry<Future<HTMLPageResponse>, CrawlerURL>> it = responses.entrySet().iterator();
        boolean PageAlreadyFetched = false;

        while (it.hasNext()) {

            final Entry<Future<HTMLPageResponse>, CrawlerURL> entry = it.next();

            try {
                HTMLPageResponse responseTemp = null;
                for (HTMLPageResponse verifiedUrl: verifiedUrls) {
                    if (verifiedUrl.getUrl().equals(entry.getValue().getUrl())) {
                        responseTemp = verifiedUrl;
                        PageAlreadyFetched = true;
                        break;
                    }
                }
                final HTMLPageResponse response = PageAlreadyFetched ? responseTemp : entry.getKey().get();
                if (HttpStatus.SC_OK == response.getResponseCode()
                        && response.getResponseType().indexOf("html") > 0) {
                    // we know that this links work
                    verifiedUrls.add(response);

                    // Add all hidden slides with links
                    var tests = response.getBody().body().getElementsByClass("slick-next slick-arrow");

                    final Set<CrawlerURL> allLinks = parser.get(response);

                    for (CrawlerURL link : allLinks) {
                        // only add if it is the same host
                        System.out.println("Host: " + host + ", linkHost" + link.getHost());
                        System.out.println("LinkUrl: " + link.getUrl() + ", onlyOnPath: " + onlyOnPath + ", notOnPath: " + notOnPath);
                        if (host.equals(link.getHost()) && link.getUrl().contains(onlyOnPath)
                                && (notOnPath.equals("") || (!link.getUrl().contains(notOnPath)))) {
                            if (!allUrls.contains(link)) {
                                nextLevel.add(link);
                                allUrls.add(link);
                            }
                        }
                    }
                } else if (HttpStatus.SC_OK != response.getResponseCode() || StatusCode.SC_SERVER_REDIRECT_TO_NEW_DOMAIN.getCode() ==  response.getResponseCode()) {
                    allUrls.remove(entry.getValue());
                    nonWorkingUrls.add(response);
                } else {
                    // it is of another content type than HTML or if it redirected to another domain
                    allUrls.remove(entry.getValue());
                }

            } catch (InterruptedException | ExecutionException e) {
                nonWorkingUrls.add(new HTMLPageResponse(entry.getValue(),
                        StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
                        Collections.<String, String>emptyMap(), "", "", 0, "", -1));
            }
        }
        return nextLevel;
    }

    /**
     * Verify that all urls in allUrls returns 200. If not, they will be removed from that set and
     * instead added to the nonworking list.
     *
     * @param allUrls all the links that has been fetched
     * @param nonWorkingUrls links that are not working
     */
    private void verifyUrls(Set<CrawlerURL> allUrls, Set<HTMLPageResponse> verifiedUrls,
                            Set<HTMLPageResponse> nonWorkingUrls, Map<String, String> requestHeaders) {

        Set<CrawlerURL> urlsThatNeedsVerification = new LinkedHashSet<CrawlerURL>(allUrls);

        urlsThatNeedsVerification.removeAll(verifiedUrls);

        final Set<Callable<HTMLPageResponse>> tasks =
                new HashSet<Callable<HTMLPageResponse>>(urlsThatNeedsVerification.size());

        for (CrawlerURL testURL : urlsThatNeedsVerification) {
            tasks.add(new HTMLPageResponseCallable(testURL, responseFetcher, true, requestHeaders, false));
        }

        try {
            // wait for all urls to verify
            List<Future<HTMLPageResponse>> responses = service.invokeAll(tasks);

            for (Future<HTMLPageResponse> future : responses) {
                if (!future.isCancelled()) {
                    HTMLPageResponse response = future.get();
                    if (response.getResponseCode() == HttpStatus.SC_OK
                            && response.getResponseType().indexOf("html") > 0) {
                        // remove, way of catching interrupted / execution e
                        urlsThatNeedsVerification.remove(response.getPageUrl());
                        verifiedUrls.add(response);
                    } else if (response.getResponseCode() == HttpStatus.SC_OK) {
                        // it is not HTML
                        urlsThatNeedsVerification.remove(response.getPageUrl());
                    } else {
                        nonWorkingUrls.add(response);
                    }
                }
            }

        } catch (InterruptedException | ExecutionException e1) {
            // TODO add some logging
            LOGGER.error(e1.getMessage());
        } // TODO Auto-generated catch block

        // TODO: We can have a delta here if the exception occur
    }

    /**
     * Verify that all urls in allUrls returns 200. If not, they will be removed from that set and
     * instead added to the nonworking list.
     *
     * @param allUrls all the links that has been fetched
     * @param nonWorkingUrls links that are not working
     */
    private Set<AssetResponse> verifyUrlsAndLoadFiles(Set<CrawlerURL> allUrls, Set<HTMLPageResponse> verifiedUrls,
                            Set<HTMLPageResponse> nonWorkingUrls, Map<String, String> requestHeaders, Set<Url> dbUrls) {

        Set<CrawlerURL> urlsThatNeedsVerification = new LinkedHashSet<CrawlerURL>();
        Set<AssetResponse> loadedAssets = new LinkedHashSet<AssetResponse>();

        String filesFilter = fileFilters != null ? fileFilters : ".xml;.zip;.dtd";
        String[] filters = filesFilter.split(";");
        if (filters.length > 0 ) {
            for (CrawlerURL url : allUrls) {
                for (String filter: filters) {
                    if (url.getUrl().toLowerCase().contains(filter.toLowerCase())) {
                        if (!url.getUrl().contains("drs")) {
                            urlsThatNeedsVerification.add(url);
                        }
                        break;
                    }
                }
            }
        }
        urlsThatNeedsVerification.removeAll(verifiedUrls);

        for (Url dbUrl : dbUrls) {
            urlsThatNeedsVerification.removeIf(urlTNeed -> urlTNeed.getUrl().equals(dbUrl.getValue())
                    && dbUrl.getLastStatusCode() == HttpStatus.SC_OK && "Asset".equals(dbUrl.getType())
                    && dbUrl.getTitle().equals(urlTNeed.getTitle())
            );
        }


        final Set<Callable<AssetResponse>> tasks =
                new HashSet<>(urlsThatNeedsVerification.size());

        // urlsThatNeedsVerification contains only needed files urls
        // Process download of files if necessary

        for (CrawlerURL testURL : urlsThatNeedsVerification) {
            String [] urlParts = testURL != null ? testURL.getUrl().split("/") : null;
            String fileName = urlParts != null ? urlParts[urlParts.length - 1] : "defaultFile";
            String downloadLocation = downloadFileLocation != null ? downloadFileLocation : "output";
            tasks.add(new AssetResponseCallable(testURL.getUrl(), testURL.getTitle(), assetFetcher, requestHeaders, testURL.getUrl(), Paths.get(downloadLocation, fileName).toString() ));
        }

        try {
            // wait for all urls to verify
            List<Future<AssetResponse>> responses = service.invokeAll(tasks);
            int responseCounter = 0;
            Object [] arrayOfurlsThatNeedsVerification = urlsThatNeedsVerification.toArray();
            for (Future<AssetResponse> future : responses) {
                if (!future.isCancelled()) {
                    AssetResponse response = future.get();
                    if (response.getResponseCode() == HttpStatus.SC_OK && response.getAssetSize() >= 0) {
                        // remove, way of catching interrupted / execution e
                        int finalResponseCounter = responseCounter;
                        urlsThatNeedsVerification.removeIf(urlTN -> urlTN.getUrl().equals(((CrawlerURL)arrayOfurlsThatNeedsVerification[finalResponseCounter]).getUrl()));
                        loadedAssets.add(response);
                        verifiedUrls.add(new HTMLPageResponse((CrawlerURL) arrayOfurlsThatNeedsVerification[responseCounter],
                                response.getResponseCode(), Collections.<String, String>emptyMap(),
                                "", "", response.getAssetSize(), "", response.getFetchTime()));
                    } else if (response.getResponseCode() == HttpStatus.SC_OK) {
                        urlsThatNeedsVerification.remove(arrayOfurlsThatNeedsVerification[responseCounter]);
                    } else {
                        nonWorkingUrls.add(new HTMLPageResponse((CrawlerURL)arrayOfurlsThatNeedsVerification[responseCounter],
                                StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
                                Collections.<String, String>emptyMap(), "", "", 0, "", -1));
                    }
                }
                responseCounter++;
            }

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage());
        }

        return loadedAssets;
    }

    private HTMLPageResponse fetchOnePage(CrawlerURL url, Map<String, String> requestHeaders) {
        return responseFetcher.get(url, true, requestHeaders, true);
    }

    private HTMLPageResponse verifyInput(String startUrl, String onlyOnPath,
                                         Map<String, String> requestHeaders) {

        final CrawlerURL pageUrl = new CrawlerURL(startUrl);

        if (pageUrl.isWrongSyntax())
            throw new IllegalArgumentException("The url " + startUrl + " isn't a valid url ");

        // verify that the first url is reachable
        final HTMLPageResponse resp = fetchOnePage(pageUrl, requestHeaders);

        if (!StatusCode.isResponseCodeOk(resp.getResponseCode()))
            throw new IllegalArgumentException("The start url: " + startUrl
                    + " couldn't be fetched, response code "
                    + StatusCode.toFriendlyName(resp.getResponseCode()));
        return resp;
    }
}