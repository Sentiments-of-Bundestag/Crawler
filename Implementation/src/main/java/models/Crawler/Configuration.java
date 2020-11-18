package models.Crawler;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;
        Configuration that = (Configuration) o;
        return configKey.equals(that.configKey) &&
                configValue.equals(that.configValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configKey, configValue);
    }
}
