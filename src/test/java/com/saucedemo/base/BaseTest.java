package com.saucedemo.base;

import com.saucedemo.config.ConfigManager;
import com.saucedemo.driver.DriverManager;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for all TestNG tests.
 *
 * <p>Owns the per-test WebDriver lifecycle (one fresh browser per test method
 * for full isolation) and exposes a ready {@link LoginPage} to subclasses.
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.initDriver();
        recordEnvironment();
        loginPage = new LoginPage(getDriver()).open();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /** Adds run metadata to the Allure report for the current test. */
    private void recordEnvironment() {
        Allure.parameter("Browser", ConfigManager.browser());
        Allure.parameter("Headless", String.valueOf(ConfigManager.headless()));
        Allure.parameter("Base URL", ConfigManager.baseUrl());
    }
}
