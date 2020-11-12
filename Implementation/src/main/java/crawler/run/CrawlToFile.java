package crawler.run;

import com.google.inject.Guice;
import com.google.inject.Injector;
import crawler.core.Crawler;
import crawler.core.CrawlerResult;
import crawler.core.CrawlerURL;
import crawler.core.HTMLPageResponse;
import crawler.module.CrawlModule;
import crawler.util.StatusCode;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpStatus;

import java.io.*;

/**
 * Crawl to File. To files will be created, one with the working urls &amp; one with the none working
 * urls. Each url will be on one new line.
 *
 * @author peter
 *
 */
public class CrawlToFile extends AbstractCrawl {

    public static final String DEFAULT_FILENAME = "output/urls.txt";
    public static final String DEFAULT_ERROR_FILENAME = "output/errorurls.txt";
    public static final String DEFAULT_DOWNLOAD_FILE_LOCATION = "output";
    private final String fileName;
    private final String errorFileName;
    private final String downloadFileLocation;
    private final boolean verbose;

    public CrawlToFile(String[] args) throws ParseException {
        super(args);
        fileName = getLine().getOptionValue("filename", DEFAULT_FILENAME);
        errorFileName = getLine().getOptionValue("errorfilename", DEFAULT_ERROR_FILENAME);
        downloadFileLocation = getLine().getOptionValue("downloadfilelocation", DEFAULT_DOWNLOAD_FILE_LOCATION);
        verbose = Boolean.parseBoolean(getLine().getOptionValue("verbose", "false"));
    }

    /**
     * Run.
     *
     * @param args the args
     */
    public static void main(String[] args) {

        try {
            final CrawlToFile crawl = new CrawlToFile(args);
            crawl.crawl();

        } catch (ParseException e) {
            System.err.print(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }

    public void crawl() {
        final Injector injector = Guice.createInjector(new CrawlModule());
        final Crawler crawler = injector.getInstance(Crawler.class);

        final CrawlerResult result = crawler.getUrls(getConfiguration());

        final StringBuilder workingUrls = new StringBuilder();
        final StringBuilder nonWorkingUrls = new StringBuilder();

        String separator = System.getProperty( "line.separator" );

        for (CrawlerURL workingUrl : result.getUrls()) {
            workingUrls.append(workingUrl.getUrl()).append(separator);
        }

        if (verbose) System.out.println("Start storing file working urls " + fileName);

        writeFile(fileName, workingUrls.toString());


        if (result.getNonWorkingUrls().size() > 0) {
            for (HTMLPageResponse nonWorkingUrl : result.getNonWorkingUrls()) {
                nonWorkingUrls.append(StatusCode.toFriendlyName(nonWorkingUrl.getResponseCode()))
                        .append(",").append(nonWorkingUrl.getUrl());
                if (nonWorkingUrl.getResponseCode() >= HttpStatus.SC_NOT_FOUND)
                    nonWorkingUrls.append(" from ").append(nonWorkingUrl.getPageUrl().getReferer());
                nonWorkingUrls.append(separator);
            }

            if (verbose) System.out.println("Start storing file non working urls " + errorFileName);
            writeFile(errorFileName, nonWorkingUrls.toString());
        }

        crawler.shutdown();
    }

    /**
     * Get the options.
     *
     * @return the specific CrawlToFile options
     */
    @Override
    protected Options getOptions() {
        final Options options = super.getOptions();

        final Option filenameOption =
                new Option("f", "the name of the output file, default name is " + DEFAULT_FILENAME
                        + " [optional]");
        filenameOption.setArgName("FILENAME");
        filenameOption.setLongOpt("filename");
        filenameOption.setRequired(false);
        filenameOption.setArgs(1);

        options.addOption(filenameOption);

        final Option errorFilenameOption =
                new Option("ef", "the name of the error output file, default name is "
                        + DEFAULT_ERROR_FILENAME + " [optional]");
        errorFilenameOption.setArgName("ERRORFILENAME");
        errorFilenameOption.setLongOpt("errorfilename");
        errorFilenameOption.setRequired(false);
        errorFilenameOption.setArgs(1);

        options.addOption(errorFilenameOption);

        final Option downloadFileLocationOption =
                new Option("df", "the default download output directory is "
                        + DEFAULT_DOWNLOAD_FILE_LOCATION + " [optional]");
        downloadFileLocationOption.setArgName("DOWNLOADFILELOCATION");
        downloadFileLocationOption.setLongOpt("downloadfilelocation");
        downloadFileLocationOption.setRequired(false);
        downloadFileLocationOption.setArgs(1);

        options.addOption(downloadFileLocationOption);

        final Option verboseOption = new Option("ve", "verbose logging, default is false [optional]");
        verboseOption.setArgName("VERBOSE");
        verboseOption.setLongOpt("verbose");
        verboseOption.setRequired(false);
        verboseOption.setArgs(1);
        verboseOption.setType(Boolean.class);

        options.addOption(verboseOption);

        return options;

    }

    private void writeFile(String fileName, String output) {
        Writer out = null;
        try {
            File outputFile = new File(fileName);
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"));
            out.write(output);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println(e);
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.err.println(e);
            }
        }
    }
}