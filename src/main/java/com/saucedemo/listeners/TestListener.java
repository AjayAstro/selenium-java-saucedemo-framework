package com.saucedemo.listeners;

import com.saucedemo.driver.DriverManager;
import com.saucedemo.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that logs the lifecycle of every test and captures a
 * screenshot (attached to Allure) whenever a test fails.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("==== Starting suite: {} ====", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> START: {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("<<< PASS : {}", testName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("<<< FAIL : {}", testName(result), result.getThrowable());
        WebDriver driver = currentDriverOrNull();
        if (driver != null) {
            ScreenshotUtils.captureAndAttach(driver, "Failure - " + testName(result));
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("<<< SKIP : {}", testName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("==== Finished suite: {} ====", context.getName());
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }

    private static WebDriver currentDriverOrNull() {
        try {
            return DriverManager.getDriver();
        } catch (IllegalStateException e) {
            return null; // driver already torn down or never started
        }
    }
}
