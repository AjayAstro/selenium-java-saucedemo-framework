package com.saucedemo.driver;

import com.saucedemo.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds fully-configured {@link WebDriver} instances for the requested browser.
 *
 * <p>Driver binaries are provisioned automatically by WebDriverManager, so no
 * manual driver setup is required on a developer machine or in CI.
 */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver create(BrowserType browser, boolean headless) {
        log.info("Creating {} driver (headless={})", browser, headless);
        return switch (browser) {
            case CHROME -> chrome(headless);
            case FIREFOX -> firefox(headless);
            case EDGE -> edge(headless);
        };
    }

    private static WebDriver chrome(boolean headless) {
        // Honour an externally supplied driver (e.g. -Dwebdriver.chrome.driver=...)
        // and only fall back to WebDriverManager when one is not already provided.
        if (isBlank(System.getProperty("webdriver.chrome.driver"))) {
            WebDriverManager wdm = WebDriverManager.chromedriver();
            String version = ConfigManager.browserVersion();
            if (!isBlank(version)) {
                wdm.browserVersion(version);
            }
            wdm.setup();
        }
        ChromeOptions options = new ChromeOptions();
        applyChromiumCommonArgs(options, headless);
        String binary = ConfigManager.browserBinary();
        if (!isBlank(binary)) {
            options.setBinary(binary);
        }
        applyExtraArgs(options::addArguments);
        return new ChromeDriver(options);
    }

    private static WebDriver edge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        return new EdgeDriver(options);
    }

    private static WebDriver firefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        return new FirefoxDriver(options);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Applies any extra, whitespace-separated browser arguments from the
     * {@code chromeArgs} property. Handy for proxies or CI-specific tuning
     * without touching code, e.g. {@code -DchromeArgs="--proxy-server=..."}.
     */
    private static void applyExtraArgs(java.util.function.Consumer<String> addArg) {
        String extra = ConfigManager.get("chromeArgs", "");
        if (!isBlank(extra)) {
            for (String arg : extra.trim().split("\\s+")) {
                addArg.accept(arg);
            }
        }
    }

    /** Args shared by Chrome/Chromium that keep runs stable in containers and CI. */
    private static void applyChromiumCommonArgs(ChromeOptions options, boolean headless) {
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-allow-origins=*",
                "--window-size=1920,1080");
        // SauceDemo triggers Chrome's "breached password" popup; disable it.
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-features=PasswordLeakDetection");
    }
}
