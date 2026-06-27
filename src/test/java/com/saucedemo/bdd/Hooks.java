package com.saucedemo.bdd;

import com.saucedemo.driver.DriverManager;
import com.saucedemo.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/** Cucumber lifecycle hooks: own the WebDriver per scenario, screenshot on failure. */
public class Hooks {

    @Before
    public void beforeScenario() {
        DriverManager.initDriver();
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            ScreenshotUtils.captureAndAttach(DriverManager.getDriver(), "Failure - " + scenario.getName());
        }
        DriverManager.quitDriver();
    }
}
