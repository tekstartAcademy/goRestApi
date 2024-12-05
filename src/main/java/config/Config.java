package config;

import java.util.ResourceBundle;

public class Config {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("application");

    public static String getBaseUrl() {
        return bundle.getString("base.url");
    }

    public static String getAuthEndpoint() {
        return bundle.getString("auth.endpoint");
    }

    public static String getClientId() {
        return bundle.getString("email");
    }

    public static String getClientSecret() {
        return bundle.getString("password");
    }
}
