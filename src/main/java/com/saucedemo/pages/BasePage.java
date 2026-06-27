package com.saucedemo.pages;

import com.saucedemo.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Foundation for every Page Object.
 *
 * <p>Centralises the driver reference, the explicit {@link WebDriverWait}, and a
 * small set of synchronised interaction helpers so concrete pages never call
 * {@code Thread.sleep} or duplicate wait logic.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(ConfigManager.explicitWaitSeconds()));
    }

    // ---- Synchronised interactions ----------------------------------------

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected List<WebElement> findAll(By locator) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        return driver.findElements(locator);
    }

    protected int countOf(By locator) {
        return driver.findElements(locator).size();
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
