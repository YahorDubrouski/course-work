package base;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new IOException("Properties file not found in resources folder");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fetch value from env variables or fallback to properties file
    public String getConfig(String key) {
        String envKey = convertToEnvKey(key);
        return System.getenv(envKey) != null ? System.getenv(envKey) : properties.getProperty(key);
    }

    // Convert property key to environment variable naming convention
    private String convertToEnvKey(String key) {
        return key.toUpperCase().replace('.', '_');
    }
}
