package crawler.core.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import crawler.core.*;
import crawler.core.assets.AssetFetcher;
import crawler.core.assets.AssetResponse;
import crawler.core.assets.AssetResponseCallable;
import crawler.core.assets.AssetsParser;
import crawler.util.StatusCode;
import models.Crawler.Url;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.service.DynamicScheduler;

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
    private final HTMLPageResponseFetcher responseFetcher;
    private final AssetFetcher assetFetcher;
    private final ExecutorService service;
    private final PageURLParser parser;
    private final AssetsParser assetsParser;
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicScheduler.class);

    /**
     * Create a new crawler.
     * @param theResponseFetcher the response fetcher to use.
     * @param theService the thread pool.
     * @param theParser the parser.
     * @param theAssetFetcher
     * @param theAssetsParser
     */
    @Inject
    public DefaultCrawler(HTMLPageResponseFetcher theResponseFetcher, ExecutorService theService,
                          PageURLParser theParser,
                          AssetFetcher theAssetFetcher,
                          AssetsParser theAssetsParser,
                          @Named(CrawlerConfiguration.FILE_FILTERS_PROPERTY_NAME) String theFileFilters,
                          @Named(CrawlerConfiguration.DEFAULT_DOWNLOAD_FILE_LOCATION_PROPERTY_NAME) String theDownloadFileLocation
                          ) {
        service = theService;
        responseFetcher = theResponseFetcher;
        parser = theParser;
        fileFilters = theFileFilters;
        downloadFileLocation = theDownloadFileLocation;
        assetFetcher = theAssetFetcher;
        assetsParser = theAssetsParser;
    }

    /**
     * Shutdown the crawler.
     */
    public void shutdown() {
        if (service != null) service.shutdown();
        if (responseFetcher != null) responseFetcher.shutdown();
    }

    /**
     * Get the urls.
     *
     * @param configuration how to perform the crawl
     * @param dbUrls list of urls from the db
     * @return the result of the crawl
     */
    public CrawlerResult getUrls(CrawlerConfiguration configuration, Set<Url> dbUrls) {
        final Map<String, String> requestHeaders = configuration.getRequestHeadersMap();
        final HTMLPageResponse resp =
                verifyInput(configuration.getStartUrl(), configuration.getOnlyOnPath(), requestHeaders);

        int level = 0;

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

        return new CrawlerResult(configuration.getStartUrl(), configuration.isVerifyUrls()
                ? workingUrls
                : allUrls, verifiedUrls, nonWorkingResponses, host, loadedAssets, assetsParser.getProtokolls(loadedAssets));

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

        String filesFilter = fileFilters != null ? fileFilters : ".xml;.zip";
        String[] filters = filesFilter.split(";");
        if (filters.length > 0 ) {
            for (CrawlerURL url : allUrls) {
                for (String filter: filters) {
                    if (url.getUrl().contains(filter)) {
                        urlsThatNeedsVerification.add(url);
                        break;
                    }
                }
            }
        }
        urlsThatNeedsVerification.removeAll(verifiedUrls);

        for (Url dbUrl : dbUrls) {
            urlsThatNeedsVerification.removeIf(urlTNeed -> urlTNeed.getUrl().equals(dbUrl.getValue())
                    && dbUrl.getLastStatusCode() == HttpStatus.SC_OK && "Asset".equals(dbUrl.getType()));
        }


        final Set<Callable<AssetResponse>> tasks =
                new HashSet<>(urlsThatNeedsVerification.size());

        // urlsThatNeedsVerification contains only needed files urls
        // Process download of files if necessary

        for (CrawlerURL testURL : urlsThatNeedsVerification) {
            String [] urlParts = testURL != null ? testURL.getUrl().split("/") : null;
            String fileName = urlParts != null ? urlParts[urlParts.length - 1] : "defaultFile";
            String downloadLocation = downloadFileLocation != null ? downloadFileLocation : "output";
            tasks.add(new AssetResponseCallable(testURL.getUrl(), assetFetcher, requestHeaders, "", downloadLocation + "/" + fileName));
        }

        try {
            // wait for all urls to verify
            List<Future<AssetResponse>> responses = service.invokeAll(tasks);

            for (Future<AssetResponse> future : responses) {
                if (!future.isCancelled()) {
                    AssetResponse response = future.get();
                    CrawlerURL responseURL = new CrawlerURL(response.getUrl());
                    if (response.getResponseCode() == HttpStatus.SC_OK && response.getAssetSize() >= 0) {
                        // remove, way of catching interrupted / execution e
                        urlsThatNeedsVerification.removeIf(urlTN -> urlTN.getUrl().equals(responseURL.getUrl()));
                        loadedAssets.add(response);
                        verifiedUrls.add(new HTMLPageResponse(new CrawlerURL(response.getUrl()),
                                response.getResponseCode(), Collections.<String, String>emptyMap(),
                                "", "", response.getAssetSize(), "", response.getFetchTime()));
                    } else if (response.getResponseCode() == HttpStatus.SC_OK) {
                        urlsThatNeedsVerification.remove(responseURL);
                    } else {
                        nonWorkingUrls.add(new HTMLPageResponse(responseURL,
                                StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
                                Collections.<String, String>emptyMap(), "", "", 0, "", -1));
                    }
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            // TODO add some logging
            LOGGER.error(e.getMessage());
        } // TODO Auto-generated catch block

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