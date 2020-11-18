package web.service;


import crawler.core.CrawlerResult;
import crawler.core.assets.AssetResponse;
import crawler.run.CrawlToFile;
import models.Crawler.Configuration;
import models.Crawler.Url;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import repositories.Crawler.ConfigurationRepository;
import repositories.Crawler.UrlRepository;
import repositories.ProtokollRepository;

import javax.annotation.PostConstruct;
import java.time.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicScheduler implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicScheduler.class);

    ScheduledTaskRegistrar scheduledTaskRegistrar;

    private final ConfigurationRepository configurationRepository;
    private final UrlRepository urlRepository;
    private final ProtokollRepository protokollRepository;

    private ScheduledFuture future;
    private Map<ScheduledFuture, Boolean> futureMap;

    public DynamicScheduler(ConfigurationRepository configurationRepository, UrlRepository urlRepository, ProtokollRepository protokollRepository) {
        this.configurationRepository = configurationRepository;
        this.urlRepository = urlRepository;
        this.protokollRepository = protokollRepository;
    }

    @PostConstruct
    public void initDatabase() {
        futureMap = new HashMap<>();
        Set<Configuration> configs = new LinkedHashSet<Configuration>();
        Optional<Configuration> dbConfig = configurationRepository.findById("next_exec_time");
        if (dbConfig.isEmpty()) {
            configs.add(new Configuration("next_exec_time", "4"));
        }
        dbConfig = configurationRepository.findById("seed_url");
        if (dbConfig.isEmpty()) {
            configs.add(new Configuration("seed_url", "https://www.bundestag.de/services/opendata"));
        }

        configurationRepository.saveAll(configs);
    }

    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    // We can have multiple tasks inside the same registrar as we can see below.
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (scheduledTaskRegistrar == null) {
            scheduledTaskRegistrar = taskRegistrar;
        }
        if (taskRegistrar.getScheduler() == null) {
            taskRegistrar.setScheduler(poolScheduler());
        }

        // Random next execution time.
        if (future == null || (future.isCancelled() && futureMap.get(future))) {
            future = taskRegistrar.getScheduler().schedule(this::scheduleDynamically, t -> {
                Calendar nextExecutionTime = new GregorianCalendar();
                Date lastActualExecutionTime = t.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                nextExecutionTime.add(Calendar.SECOND, getNextExecutionTime()); // This is where we set the next execution time.
                return nextExecutionTime.getTime();
            });
        }

        // Fixed next execution time.
        if (future == null || (future.isCancelled() && futureMap.get(future))) {
            future = taskRegistrar.getScheduler().schedule(() -> scheduleFixed(7), t -> {
                Calendar nextExecutionTime = new GregorianCalendar();
                Date lastActualExecutionTime = t.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                nextExecutionTime.add(Calendar.SECOND, 7); // This is where we set the next execution time.
                return nextExecutionTime.getTime();
            });
        }

        // Next execution time is taken from DB, so if the value in DB changes, next execution time will change too.
        /*if (future == null || (future.isCancelled() && futureMap.get(future))) {
            future = taskRegistrar.getScheduler().schedule(() -> scheduledDatabase(repo.findById("next_exec_time").get().getConfigValue()), t -> {
                Calendar nextExecutionTime = new GregorianCalendar();
                Date lastActualExecutionTime = t.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                nextExecutionTime.add(Calendar.SECOND, Integer.parseInt(repo.findById("next_exec_time").get().getConfigValue()));
                return nextExecutionTime.getTime();
            });
        }*/

        // or cron way, you can also get the expression from DB or somewhere else just like we did above.
        /*if (future == null || (future.isCancelled() && futureMap.get(future))) {
            future = taskRegistrar.getScheduler().schedule(() -> scheduleCron(repo.findById("next_exec_time").get().getConfigValue()), t -> {
                CronTrigger crontrigger = new CronTrigger(repo.findById("next_exec_time").get().getConfigValue());
                return crontrigger.nextExecutionTime(t);
            });
        }*/
    }

    // Get scheduled Task by id
    public Map.Entry<ScheduledFuture, Boolean> getScheduledTaskById(int id) {
        if (futureMap == null || futureMap.size() == 0 || futureMap.size() <= id)
            return null;
        Map.Entry<ScheduledFuture, Boolean> futureTask = null;
        int counter = 0;
        for (Map.Entry<ScheduledFuture, Boolean> entry : futureMap.entrySet()) {
            if (counter == id) {
                futureTask = entry;
                break;
            }
            counter++;
        }

        return futureTask;
    }

    // Start process with frequency in second
    public int scheduleWithFrequency(int frequency) {
        future = poolScheduler().schedule(() -> scheduleFixed(frequency), t -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = t.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            nextExecutionTime.add(Calendar.SECOND, frequency); // This is where we set the next execution time.
            return nextExecutionTime.getTime();
        });
        futureMap.put(future, true);
        return futureMap.size() - 1;
    }

    // startDate and endDate are only calendar date and time indicates at which hour/minute of the day.
    public int scheduleAt(LocalDate startDate, LocalDate endDate, LocalTime time) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(endDate)) {
            if (now.isBefore(startDate)) {
                now = startDate;
            }
            LocalDateTime current = now.atTime(time);
            ZoneId zone = ZoneId.of("Europe/Berlin");
            ZoneOffset zoneOffSet = zone.getRules().getOffset(current);
            Instant nextRunTime = current.toInstant(zoneOffSet);
            future = poolScheduler().schedule(() -> scheduleSingleCrawlJob(nextRunTime), nextRunTime);
            futureMap.put(future, true);
        }
        return futureMap.size() - 1;
    }

    // Default test Task
    public int scheduleDefaultTask() {
        return this.scheduleAt(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), LocalTime.now().plusSeconds(5));
    }

    public void scheduleSingleCrawlJob(Instant nextRunTime) {
        // This is your real code to be scheduled
        LOGGER.info("scheduleSingleCrawlJob: A single CrawlJob have been scheduled by user & will start in 5 seconds");
        Optional<Configuration> seedUrlConfig = configurationRepository.findById("seed_url");
        String[] args = {"-u", seedUrlConfig.isEmpty() ? "https://www.bundestag.de/services/opendata" : seedUrlConfig.get().getConfigValue()};
        try {
            Optional<Url> startUrl = urlRepository.findById(args[1]);
            final CrawlToFile crawl = new CrawlToFile(args);
            CrawlerResult crawlerResult = crawl.crawl(new LinkedHashSet<>(urlRepository.findAll()));
            if (crawlerResult != null) {
                Set<Url> dbUrls = new LinkedHashSet<Url>();
                for (AssetResponse assetResponse : crawlerResult.getLoadedAssets()) {
                    dbUrls.add(new Url(crawlerResult.getStartPointHost(), assetResponse.getUrl(), new Date(System.currentTimeMillis()), assetResponse.getResponseCode(), assetResponse.getAssetPath(), assetResponse.getAssetSize(), "Asset"));
                }

                if (startUrl.isEmpty()) {
                    dbUrls.add(new Url(crawlerResult.getStartPointHost(), crawlerResult.getStartPoint(), new Date(System.currentTimeMillis()), HttpStatus.SC_OK, "", -1, "Page"));
                }

                urlRepository.saveAll(dbUrls);

                if (startUrl.isPresent()) {
                    Url existingStartUrl = startUrl.get();
                    existingStartUrl.setLastRequestTime(new Date(System.currentTimeMillis()));
                    urlRepository.save(existingStartUrl);
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void scheduleDynamically() {
        LOGGER.info("scheduleDynamically: Next execution time of this changes every time between 1 and 5 seconds");
    }

    // I added this to show that one taskRegistrar can have multiple different tasks.
    // And each of those tasks can have their own next execution time.
    public void scheduleFixed(int frequency) {
        LOGGER.info("scheduleFixed: Next execution time of this will always be {} seconds", frequency);
    }

    public void scheduledDatabase(String time) {
        LOGGER.info("scheduledDatabase: Next execution time of this will be taken from DB -> {}", time);
    }

    // Only reason this method gets the cron as parameter is for debug purposes.
    public void scheduleCron(String cron) {
        LOGGER.info("scheduleCron: Next execution time of this taken from cron expression -> {}", cron);
    }

    // This is only to show that next execution time can be changed on the go with SchedulingConfigurer.
    // This can not be done via @Scheduled annotation.
    public int getNextExecutionTime() {
        return new Random().nextInt(5) + 1;
    }

    public Map<ScheduledFuture, Boolean> getScheduledTasksList(){
        return futureMap;
    }

    /**
     * @param mayInterruptIfRunning {@code true} if the thread executing this task
     * should be interrupted; otherwise, in-progress tasks are allowed to complete
     */
    public void cancelFuture(boolean mayInterruptIfRunning, ScheduledFuture future) {
        LOGGER.info("Cancelling a future");
        future.cancel(mayInterruptIfRunning); // set to false if you want the running task to be completed first.
        futureMap.put(future, false);
    }

    public void activateFuture(ScheduledFuture future) {
        LOGGER.info("Re-Activating a future");
        futureMap.put(future, true);
        configureTasks(scheduledTaskRegistrar);
    }

    public int cancelById(int id) {
        Map.Entry<ScheduledFuture, Boolean> futureTask = getScheduledTaskById(id);

        if (futureTask == null)
            return -1;
        else {
            if (futureTask.getValue())
                cancelFuture(true, futureTask.getKey());
            return  id;
        }
    }
    public void cancelAll() {
        cancelFuture(true, future);
    }

    public void activateAll() {
        activateFuture(future);
    }
}
