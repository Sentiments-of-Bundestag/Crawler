package crawler.run;

import crawler.core.CrawlerConfiguration;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Abstract crawl class, used to setup default args options.
 *
 *
 */
public abstract class AbstractCrawl extends AbstractRunner {

    private static final String URL = "url";
    private static final String LEVEL = "level";
    private static final String FOLLOW_PATH = "followPath";
    private static final String NO_FOLLOW_PATH = "notFollowPath";
    private static final String VERIFY = "verify";
    private static final String REQUEST_HEADERS = "requestHeaders";
    private static final String FILE_FILTERS = "fileFilters";
    private final CrawlerConfiguration configuration;


    /**
     * Create a crawl object, will fetch the args that is needed.
     *
     * @param args containing needed args
     * @throws ParseException if the input parameter couldn't be parsed
     */
    public AbstractCrawl(String[] args) throws ParseException {
        super(args);

        configuration =
                CrawlerConfiguration
                        .builder()
                        .setMaxLevels(
                                Integer.parseInt(getLine().getOptionValue(LEVEL,
                                        Integer.toString(CrawlerConfiguration.DEFAULT_CRAWL_LEVEL))))
                        .setVerifyUrls(
                                Boolean.parseBoolean(getLine().getOptionValue(VERIFY,
                                        Boolean.toString(CrawlerConfiguration.DEFAULT_SHOULD_VERIFY_URLS))))
                        .setOnlyOnPath(getLine().getOptionValue(FOLLOW_PATH, ""))
                        .setNotOnPath(getLine().getOptionValue(NO_FOLLOW_PATH, ""))
                        .setRequestHeaders(getLine().getOptionValue(REQUEST_HEADERS, ""))
                        .setFileFilters(getLine().getOptionValue(FILE_FILTERS, ""))
                        .setStartUrl(getLine().getOptionValue(URL)).build();

    }


    /**
     * Get hold of the default options.
     *
     * @return the options that needs to run a crawl
     */
    @Override
    protected Options getOptions() {
        final Options options = super.getOptions();

        final Option urlOption =
                new Option("u",
                        "the page that is the startpoint of the crawl, examle http://mydomain.com/mypage");
        urlOption.setLongOpt(URL);
        urlOption.setArgName("URL");
        urlOption.setRequired(true);
        urlOption.setArgs(1);

        options.addOption(urlOption);

        final Option levelOption =
                new Option("l", "how deep the crawl should be done, default is "
                        + CrawlerConfiguration.DEFAULT_CRAWL_LEVEL + " [optional]");
        levelOption.setArgName("LEVEL");
        levelOption.setLongOpt(LEVEL);
        levelOption.setRequired(false);
        levelOption.setArgs(1);
        options.addOption(levelOption);

        final Option followOption = new Option("p", "stay on this path when crawling [optional]");
        followOption.setArgName("PATH");
        followOption.setLongOpt(FOLLOW_PATH);
        followOption.setRequired(false);
        followOption.setArgs(1);
        options.addOption(followOption);

        final Option noFollowOption =
                new Option("np", "no url:s on this path will be crawled [optional]");
        noFollowOption.setArgName("NOPATH");
        noFollowOption.setLongOpt(NO_FOLLOW_PATH);
        noFollowOption.setRequired(false);
        noFollowOption.setArgs(1);
        options.addOption(noFollowOption);

        final Option verifyOption =
                new Option("v", "verify that all links are returning 200, default is set to "
                        + CrawlerConfiguration.DEFAULT_SHOULD_VERIFY_URLS + " [optional]");
        verifyOption.setArgName("VERIFY");
        verifyOption.setLongOpt(VERIFY);
        verifyOption.setRequired(false);
        verifyOption.setArgs(1);
        options.addOption(verifyOption);

        final Option requestHeadersOption =
                new Option("rh",
                        "the request headers by the form of header1:value1@header2:value2 [optional]");
        requestHeadersOption.setArgName("REQUEST-HEADERS");
        requestHeadersOption.setLongOpt(REQUEST_HEADERS);
        requestHeadersOption.setRequired(false);
        requestHeadersOption.setArgs(1);
        options.addOption(requestHeadersOption);

        final Option fileFiltersOption =
                new Option("ff",
                        "the file filters by the form of .xml;.zip [optional]");
        fileFiltersOption.setArgName("FILE-FILTERS");
        fileFiltersOption.setLongOpt(FILE_FILTERS);
        fileFiltersOption.setRequired(false);
        fileFiltersOption.setArgs(1);
        options.addOption(fileFiltersOption);

        return options;
    }

    protected CrawlerConfiguration getConfiguration() {
        return configuration;
    }
}