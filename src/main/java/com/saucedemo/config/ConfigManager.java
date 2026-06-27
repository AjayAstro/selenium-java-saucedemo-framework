package com.saucedemo.config;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.util.Properties;

/**
 * Central, read-only access point for all framework configuration.
 *
 * <p>Values are resolved in the following order of precedence:
 * <ol>
 *     <li>JVM system property (e.g. {@code -Dbrowser=firefox})</li>
 *     <li>Environment variable</li>
 *     <li>{@code config/config.properties} on the classpath</li>
 * </ol>
 *
 * This lets the same build run unchanged locally and in CI, while still being
 * fully overridable from the command line.
 */
public final class ConfigManager {

    private static final String CONFIG_FILE = "config/config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigManager() {
        // Utility class - no instances.
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input =
                     ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found on classpath: " + CONFIG_FILE);
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load configuration file: " + CONFIG_FILE, e);
        }
    }

    /** Returns the resolved value for {@code key} or throws if it is absent. */
    public static String get(String key) {
        String value = resolve(key);
        if (value == null) {
            throw new IllegalStateException("Missing configuration property: " + key);
        }
        return value.trim();
    }

    /** Returns the resolved value for {@code key} or {@code defaultValue} when absent/blank. */
    public static String get(String key, String defaultValue) {
        String value = resolve(key);
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    private static String resolve(String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem;
        }
        String fromEnv = System.getenv(key);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return PROPERTIES.getProperty(key);
    }

    // ---- Convenience accessors (typed, self-documenting) -------------------

    public static String baseUrl()            { return get("baseUrl"); }
    public static String browser()            { return get("browser", "chrome").toLowerCase(); }
    public static boolean headless()          { return getBoolean("headless"); }
    public static boolean maximize()          { return getBoolean("maximize"); }
    public static int explicitWaitSeconds()   { return getInt("explicitWaitSeconds"); }
    public static int implicitWaitSeconds()   { return getInt("implicitWaitSeconds"); }
    public static int pageLoadTimeoutSeconds(){ return getInt("pageLoadTimeoutSeconds"); }
    public static String browserBinary()      { return get("browserBinary", System.getenv("CHROME_BIN")); }
    public static String browserVersion()     { return get("browserVersion", null); }

    public static String standardUser()       { return get("standardUser"); }
    public static String lockedOutUser()      { return get("lockedOutUser"); }
    public static String problemUser()        { return get("problemUser"); }
    public static String password()           { return get("password"); }
}
