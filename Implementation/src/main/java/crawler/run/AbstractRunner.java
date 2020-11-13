package crawler.run;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class that implements the basic configuration used for every runner.
 *
 *
 */
public abstract class AbstractRunner {

    /**
     * Used for converting milliseconds to seconds.
     */
    protected static final int MILLISECONDS_PER_SECOND = 1000;

    private final CommandLineParser clp = new GnuParser();

    private final CommandLine line;

    /**
     * Setup general default values needed when running url checker.
     *
     * @param args the args that is default for all runners
     * @throws ParseException if the input parameter couldn't be parsed
     */
    protected AbstractRunner(String[] args) throws ParseException {

        try {
            line = clp.parse(this.getOptions(), args);

        } catch (MissingOptionException moe) {

            final HelpFormatter hf = new HelpFormatter();
            hf.printHelp(this.getClass().getSimpleName(), getOptions(), true);
            throw moe;
        }

    }

    /**
     * Get the command line, used when fetching options.
     *
     * @return the command line
     */
    protected CommandLine getLine() {
        return line;
    }

    /**
     * Get the options.
     *
     * @return the basic options
     */
    protected Options getOptions() {

        return new Options();
    }

}