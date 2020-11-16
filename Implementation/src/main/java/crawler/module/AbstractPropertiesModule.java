package crawler.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Base class for modules, fetches and loads the property file. By default, all properties are read
 * from {@value #PROPERTY_FILE}, and each property can be overridden by adding the same property as
 * a System property.
 *
 */
public abstract class AbstractPropertiesModule extends AbstractModule {

    /**
     * The properties file in the class path. You can override these properties by system properties.
     */
    protected static final String PROPERTY_FILE = "crawler.properties";

    /**
     * Properties read from {@link #PROPERTY_FILE}.
     *
     */
    private final Properties properties = new Properties();

    @Override
    protected void configure() {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/" + PROPERTY_FILE);
            properties.load(is);

            // override by file in the running dir
            File localFile =
                    new File(new File(System.getProperty("crawler.propertydir", ".")),
                            PROPERTY_FILE);

            if (localFile.exists()) {
                InputStream in = new FileInputStream(localFile);
                properties.load(in);
            }


            // override the properties by setting a system property
            properties.putAll(System.getProperties());

            Names.bindProperties(binder(), properties);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Get the properties for this module.
     *
     * @return the properties
     */
    protected Properties getProperties() {
        return properties;
    }

}