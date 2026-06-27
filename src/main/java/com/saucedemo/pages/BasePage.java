package com.saucedemo.pages;

import com.saucedemo.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
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

    /**
     * Clicks an element after scrolling it into view, falling back to a
     * JavaScript click if the native click is intercepted (e.g. by a sticky
     * header or an in-flight animation). This keeps interactions reliable in
     * headless/CI runs without resorting to sleeps.
     */
    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            jsClick(element);
        }
    }

    /** Dispatches a click via JavaScript; robust against animated/offset elements. */
    protected void jsClick(By locator) {
        jsClick(wait.until(ExpectedConditions.presenceOfElementLocated(locator)));
    }

    private void jsClick(WebElement element) {
        scrollIntoView(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
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
