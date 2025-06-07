package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RateLimiterConfig {
    private static final Map<String, Integer> crmRateMap = new HashMap<>();

    static {
        try {
            Properties props = new Properties();
            props.load(RateLimiterConfig.class.getClassLoader().getResourceAsStream("rate-limiter.properties"));
            for (String crm : props.stringPropertyNames()) {
                crmRateMap.put(crm, Integer.parseInt(props.getProperty(crm)));
            }
        } catch (IOException e) {
            System.err.println("Failed to load rate limiter config: " + e.getMessage());
        }
    }

    public static int getRateFor(String crm) {
        return crmRateMap.getOrDefault(crm, 1); // Default fallback
    }
}
