package com.saucedemo.bdd.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG entry point for the Cucumber BDD layer.
 *
 * <p>Runs every {@code .feature} under {@code src/test/resources/features} using
 * the step definitions and hooks in {@code com.saucedemo.bdd}. Results feed the
 * same Allure report as the TestNG suite.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.saucedemo.bdd"},
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class CucumberRunnerTest extends AbstractTestNGCucumberTests {
}
