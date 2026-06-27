package com.saucedemo.utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/** Captures screenshots and attaches them to the Allure report. */
public final class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);

    private ScreenshotUtils() {
    }

    /**
     * Takes a PNG screenshot and attaches it to the current Allure test.
     *
     * @param driver the active driver
     * @param name   a human-readable attachment name (e.g. the failing test name)
     * @return the raw PNG bytes, or {@code null} if capture failed
     */
    public static byte[] captureAndAttach(WebDriver driver, String name) {
        if (driver == null) {
            return null;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
            return screenshot;
        } catch (Exception e) {
            log.warn("Failed to capture screenshot '{}': {}", name, e.getMessage());
            return null;
        }
    }
}
