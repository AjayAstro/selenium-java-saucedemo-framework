package com.saucedemo.driver;

import com.saucedemo.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Thread-safe holder for the active {@link WebDriver}.
 *
 * <p>Each test thread gets its own driver via {@link ThreadLocal}, which keeps
 * the framework safe for parallel TestNG execution.
 */
public final class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    /** Initialises a driver for the current thread using the resolved configuration. */
    public static void initDriver() {
        if (DRIVER.get() != null) {
            return;
        }
        BrowserType browser = BrowserType.from(ConfigManager.browser());
        WebDriver driver = DriverFactory.create(browser, ConfigManager.headless());

        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigManager.pageLoadTimeoutSeconds()));
        int implicitWait = ConfigManager.implicitWaitSeconds();
        if (implicitWait > 0) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        }
        if (ConfigManager.maximize() && !ConfigManager.headless()) {
            driver.manage().window().maximize();
        }
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialised for this thread. Call initDriver() first.");
        }
        return driver;
    }

    /** Quits the driver and clears the thread-local reference. */
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }
}
