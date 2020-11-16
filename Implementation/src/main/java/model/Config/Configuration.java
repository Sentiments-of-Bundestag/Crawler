package model.Config;

import org.springframework.data.annotation.Id;

public class Configuration {

    @Id
    String  configKey;

    String  configValue;

    public Configuration() {
    }

    public Configuration(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[configKey=%s, configValue='%s']",
                configKey, configValue);
    }
}
