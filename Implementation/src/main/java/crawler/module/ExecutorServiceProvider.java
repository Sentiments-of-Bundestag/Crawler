package crawler.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provide a Executor service.
 *
 */
public class ExecutorServiceProvider implements Provider<ExecutorService> {

    /**
     * The number of threads used in this executor service.
     */
    private final int nrOfThreads;

    /**
     * Create a new ExecutorServiceProvider.
     *
     * @param maxNrOfThreads the number of thread in this executor.
     */
    @Inject
    public ExecutorServiceProvider(
            @Named("crawler.threadsinworkingpool") int maxNrOfThreads) {
        nrOfThreads = maxNrOfThreads;
    }

    /**
     * Get the service.
     *
     * @return the executor.
     */
    public ExecutorService get() {
        return Executors.newFixedThreadPool(nrOfThreads);
    }

}