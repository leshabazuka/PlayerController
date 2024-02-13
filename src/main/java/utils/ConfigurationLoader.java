package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationLoader {
    public static String getProperty(String key) {
        Properties properties = new Properties();
        String property = null;
        try (InputStream input = ConfigurationLoader.class.getClassLoader().getResourceAsStream("configuration.properties")) {
            if (input == null) {
                return null;
            }
            properties.load(input);
            property = properties.getProperty(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return property;
    }
}
