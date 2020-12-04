package web.service;


import crawler.core.CrawlerResult;
import crawler.core.HTMLPageResponse;
import crawler.core.assets.AssetResponse;
import crawler.run.CrawlToFile;
import models.Crawler.Configuration;
import models.Crawler.Notification;
import models.Crawler.Url;
import models.Person.Person;
import models.Protokoll;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import repositories.Crawler.ConfigurationRepository;
import repositories.Crawler.UrlRepository;
import repositories.Person.PersonRepository;
import repositories.ProtokollRepository;
import javax.annotation.PostConstruct;
import java.time.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicScheduler implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicScheduler.class);
    private static final int DEFAULT_SLEEP_RANGE = 14400; // correspond to 4 hours
    private Random random = new Random();

    ScheduledTaskRegistrar scheduledTaskRegistrar;

    private final ConfigurationRepository configurationRepository;
    private final UrlRepository urlRepository;
    private final ProtokollRepository protokollRepository;
    private final PersonRepository personRepository;

    private ScheduledFuture future;
    private Map<ScheduledFuture, Boolean> futureMap;

    public DynamicScheduler(ConfigurationRepository configurationRepository, UrlRepository urlRepository, ProtokollRepository protokollRepository, PersonRepository personRepository) {
        this.configurationRepository = configurationRepository;
        this.urlRepository = urlRepository;
        this.protokollRepository = protokollRepository;
        this.personRepository = personRepository;
    }

    @PostConstruct
    public void initDatabase() {
        futureMap = new HashMap<>();
        Set<Configuration> configs = new LinkedHashSet<Configuration>();
        Optional<Configuration> dbConfig = configurationRepository.findById("next_exec_time");
        if (dbConfig.isEmpty()) {
            configs.add(new Configuration("next_exec_time", "0 0 23 ? * MON-FRI"));
        }
        dbConfig = configurationRepository.findById("seed_url");
        if (dbConfig.isEmpty()) {
            configs.add(new Configuration("seed_url", "https://www.bundestag.de/services/opendata"));
        }

        dbConfig = configurationRepository.findById("notification_server_url");
        if (dbConfig.isEmpty()) {
            configs.add(new Configuration("notification_server_url", "http://infosys2.f4.htw-berlin.de:9001/cme/data"));
        }

        dbConfig = configurationRepository.findById("notification_server_authorization");
        if (dbConfig.isEmpty()) {
            configs.add(new Configuration("notification_server_authorization", ""));
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
    public int scheduleAt(String seedUrl, LocalDate startDate, LocalDate endDate, LocalTime time) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(endDate)) {
            if (now.isBefore(startDate)) {
                now = startDate;
            }
            LocalDateTime current = now.atTime(time);
            ZoneId zone = ZoneId.of("Europe/Berlin");
            ZoneOffset zoneOffSet = zone.getRules().getOffset(current);
            Instant nextRunTime = current.toInstant(zoneOffSet);
            future = poolScheduler().schedule(() -> scheduleSingleCrawlJob(seedUrl), nextRunTime);
            futureMap.put(future, true);
        }
        return futureMap.size() - 1;
    }

    // Default cron task
    public int scheduleDefaultCron() {
        // Fire at 11pm every Monday, Tuesday, Wednesday, Thursday and Friday: 0 0 23 ? * MON-FRI
        // This config will be loaded from db and can also be configured there: next_exec_time
        Optional<Configuration> dbConfig = configurationRepository.findById("next_exec_time");
        if (dbConfig.isPresent()) {
            future = poolScheduler().schedule(() -> scheduleCron(dbConfig.get().getConfigValue()), t -> {
                CronTrigger crontrigger = new CronTrigger(dbConfig.get().getConfigValue());
                return crontrigger.nextExecutionTime(t);
            });
            futureMap.put(future, true);
            return futureMap.size() - 1;
        }

        return -1;
    }

    // Default single Task
    public int scheduleDefaultTask(String seedUrl, int frequency) {
        return this.scheduleAt(seedUrl, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), LocalTime.now().plusSeconds(frequency));
    }

    public void scheduleSingleCrawlJob(String seedUrl) {
        // This is your real code to be scheduled
        LOGGER.info("scheduleSingleCrawlJob: A single CrawlJob have been scheduled by user & will start in 5 seconds");

        Optional<Configuration> seedUrlConfig = configurationRepository.findById("seed_url");
        String[] args = {"-u", "default".equals(seedUrl) ? seedUrlConfig.get().getConfigValue() : seedUrl};
        try {
            Optional<Url> startUrl = urlRepository.findById(args[1]);
            final CrawlToFile crawl = new CrawlToFile(args);
            Set<Person> dbStammdaten = new LinkedHashSet<>(personRepository.findAll());
            CrawlerResult crawlerResult = crawl.crawl(new LinkedHashSet<>(urlRepository.findAll()), dbStammdaten, true);
            if (crawlerResult != null) {
                Set<Url> dbUrls = new LinkedHashSet<Url>();
                for (AssetResponse assetResponse : crawlerResult.getLoadedAssets()) {
                    dbUrls.add(new Url(crawlerResult.getStartPointHost(), assetResponse.getUrl(), assetResponse.getTitle(), new Date(System.currentTimeMillis()), assetResponse.getResponseCode(), assetResponse.getAssetPath(), assetResponse.getAssetSize(), "Asset"));
                }

                if (startUrl.isEmpty()) {
                    dbUrls.add(new Url(crawlerResult.getStartPointHost(), crawlerResult.getStartPoint(), "", new Date(System.currentTimeMillis()), HttpStatus.SC_OK, "", -1, "Page"));
                } else {
                    Url existingStartUrl = startUrl.get();
                    existingStartUrl.setLastRequestTime(new Date(System.currentTimeMillis()));
                    urlRepository.save(existingStartUrl);
                }

                urlRepository.saveAll(dbUrls);

                // check if dbStammdaten is different from the ones provided by loaderStammdaten crawlerResult
                Set<Person> loadedStammdaten = crawlerResult.getLoaderStammdaten();
                if (dbStammdaten.size() != loadedStammdaten.size() || !dbStammdaten.equals(loadedStammdaten)) {
                    // add & update person if already existing
                    personRepository.saveAll(loadedStammdaten);
                }

                // Save downloaded and parsed protokolls to DB
                if (crawlerResult.getLoadedProtokolls() != null && crawlerResult.getLoadedProtokolls().size() > 0) {
                    protokollRepository.saveAll(crawlerResult.getLoadedProtokolls());
                }

                // Now send notification for all Protokolls in the db, where notified is not true
                Optional<Configuration> notificationUrlConfig = configurationRepository.findById("notification_server_url");
                String notificationUrl = notificationUrlConfig.get().getConfigValue();
                Optional<Configuration> notificationAuthorizationConfig = configurationRepository.findById("notification_server_authorization");
                String notificationAuthorizationString = notificationAuthorizationConfig.get().getConfigValue();
                // Set credential
                String encodedAuthorization = Base64.getEncoder().withoutPadding().encodeToString(notificationAuthorizationString.getBytes());

                Set<Integer> protokollIds = new LinkedHashSet<>();
                Set<Protokoll> protokolls = new LinkedHashSet<>(protokollRepository.findAll());
                for (Protokoll protokoll : protokolls) {
                    if (!protokoll.getNotified()) {
                        protokollIds.add(protokoll.getId());
                    }
                }
                if (protokollIds.size() > 0) {
                    Notification notification = new Notification(protokollIds);

                    // Try to notify
                    HTMLPageResponse notificationResponse = crawl.SendNotification(notificationUrl, encodedAuthorization, notification);

                    if (notificationResponse.getResponseCode() == HttpStatus.SC_OK) {
                        // Successful notified
                        for (Protokoll protokoll : protokolls) {
                            protokoll.setNotified(true);
                        }
                        protokollRepository.saveAll(protokolls);
                    } else {
                        // Setup retry process or wait for the next crawl
                    }
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

    // Only reason this method gets the cron as parameter is for debug purposes.
    public void scheduleCron(String cron) {

        // Setup sleep time randomly
        int randomSleep = random.nextInt(DEFAULT_SLEEP_RANGE + 1) + 1;
        try {
            Thread.sleep(randomSleep * 1000);

            // After the sleep run crawl-task
            scheduleSingleCrawlJob("default");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("scheduleCron: Next execution time of this taken from cron expression -> {}, RandomSleep in minutes: {}, CurrentTime: {}", cron, randomSleep / 60, new Date());
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
