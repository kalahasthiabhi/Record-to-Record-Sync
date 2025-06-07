package config;

import model.CRM;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RateLimiterConfig {
    private static final Map<CRM, Integer> crmRateMap = new HashMap<>();

    static {
        try {
            Properties props = new Properties();
            props.load(RateLimiterConfig.class.getClassLoader().getResourceAsStream("rate-limiter.properties"));
            for (String crmString : props.stringPropertyNames()) {
                try {
                    CRM crm = CRM.valueOf(crmString.toUpperCase());
                    crmRateMap.put(crm, Integer.parseInt(props.getProperty(crm.name())));
                } catch (IllegalArgumentException e) {
                    System.err.println("invalid crm found in the properties: " + crmString.toUpperCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load rate limiter config: " + e.getMessage());
        }
    }

    public static int getRateFor(CRM crm) {
        return crmRateMap.getOrDefault(crm, 1); // Default fallback
    }

    public static void overrideRate(CRM crm, int rate) {
        crmRateMap.put(crm, rate);
    }

}
