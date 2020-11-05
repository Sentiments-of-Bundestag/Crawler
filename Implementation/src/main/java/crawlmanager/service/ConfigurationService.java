package crawlmanager.service;

import model.Config.Configuration;
import model.Config.Constants;
import repository.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ConfigurationService is responsible for loading and checking configuration parameters.
 *
 * @author mbcoder
 *
 */
@Service
public class ConfigurationService {

    private static final Logger LOGGER  = LoggerFactory.getLogger(ConfigurationService.class);

    ConfigRepository configRepository;

    private final Map<String, Configuration> configurationList;

    private final List<String> mandatoryConfigs;

    @Autowired
    public ConfigurationService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
        this.configurationList = new ConcurrentHashMap<>();
        this.mandatoryConfigs = new ArrayList<>();
        this.mandatoryConfigs.add(Constants.CONFIG_KEY_REFRESH_RATE_CONFIG);
    }

    /**
     * Loads configuration parameters from Database
     */
    @PostConstruct
    public void loadConfigurations() {
        LOGGER.debug("Scheduled Event: Configuration table loaded/updated from database");
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration Parameters:");
        List<Configuration> configs = configRepository.findAll();
        for (Configuration configuration : configs) {
            sb.append("\n").append(configuration.getConfigKey()).append(":").append(configuration.getConfigValue());
            this.configurationList.put(configuration.getConfigKey(), configuration);
        }
        LOGGER.debug(sb.toString());

        checkMandatoryConfigurations();
    }

    public Configuration getConfiguration(String key) {
        return configurationList.get(key);
    }

    /**
     * Checks if the mandatory parameters are exists in Database
     */
    public void checkMandatoryConfigurations() {
        for (String mandatoryConfig : mandatoryConfigs) {
            AtomicBoolean exists = new AtomicBoolean(false);
            for (Map.Entry<String, Configuration> pair : configurationList.entrySet()) {
                if (pair.getKey().equalsIgnoreCase(mandatoryConfig) && !pair.getValue().getConfigValue().isEmpty()) {
                    exists.set(true);
                }
            }
            if (!exists.get()) {
                String errorLog = String.format("A mandatory Configuration parameter is not found in DB: %s", mandatoryConfig);
                LOGGER.error(errorLog);
            }
        }

    }

}
