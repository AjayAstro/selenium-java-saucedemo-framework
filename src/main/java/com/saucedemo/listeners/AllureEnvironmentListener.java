package com.saucedemo.listeners;

import com.saucedemo.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Enriches the Allure report at suite start by writing the run environment
 * (browser, OS, Java, base URL) and copying the defect-category definitions
 * into the results directory.
 *
 * <p>These two files power Allure's "Environment" and "Categories" widgets,
 * turning the report into something a non-engineer can read at a glance.
 */
public class AllureEnvironmentListener implements ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(AllureEnvironmentListener.class);
    private static final String RESULTS_DIR_PROPERTY = "allure.results.directory";
    private static final String DEFAULT_RESULTS_DIR = "target/allure-results";
    private static final String CATEGORIES_FILE = "categories.json";

    @Override
    public void onStart(ISuite suite) {
        Path resultsDir = Path.of(System.getProperty(RESULTS_DIR_PROPERTY, DEFAULT_RESULTS_DIR));
        try {
            Files.createDirectories(resultsDir);
            writeEnvironment(resultsDir);
            copyCategories(resultsDir);
        } catch (IOException e) {
            log.warn("Could not write Allure metadata to {}: {}", resultsDir, e.getMessage());
        }
    }

    private void writeEnvironment(Path resultsDir) throws IOException {
        Properties env = new Properties();
        env.setProperty("Browser", ConfigManager.browser());
        env.setProperty("Headless", String.valueOf(ConfigManager.headless()));
        env.setProperty("Base.URL", ConfigManager.baseUrl());
        env.setProperty("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        env.setProperty("Java.Version", System.getProperty("java.version"));
        try (Writer writer = Files.newBufferedWriter(resultsDir.resolve("environment.properties"))) {
            env.store(writer, "Allure environment");
        }
    }

    private void copyCategories(Path resultsDir) throws IOException {
        try (InputStream categories =
                     getClass().getClassLoader().getResourceAsStream(CATEGORIES_FILE)) {
            if (categories != null) {
                Files.copy(categories, resultsDir.resolve(CATEGORIES_FILE),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
