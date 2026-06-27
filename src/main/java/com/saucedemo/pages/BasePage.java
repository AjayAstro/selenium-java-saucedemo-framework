package com.saucedemo.pages;

import com.saucedemo.config.ConfigManager;
import org.openqa.selenium.By;
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
     * Waits until the element is clickable, scrolls it into view and clicks it
     * via a scripted dispatch. A scripted click reliably reaches React's
     * synthetic event handlers in headless/CI runs, where a native click can be
     * silently swallowed by an overlapping node or an in-flight animation.
     */
    protected void click(By locator) {
        jsClick(waitForClickable(locator));
    }

    /** Dispatches a click via JavaScript on an element located by presence. */
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

    /**
     * Types into a field and makes sure the value actually registered.
     *
     * <p>Tries a normal {@code sendKeys} first (realistic input). The SauceDemo
     * React form can drop programmatic keystrokes on the first field right after
     * the cart&rarr;checkout route change; when that happens the value is set
     * through the input's native setter and an {@code input} event is fired so
     * React's controlled-component state picks it up.
     */
    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        if (!text.equals(element.getDomProperty("value"))) {
            setValueViaReact(element, text);
        }
    }

    private void setValueViaReact(WebElement element, String text) {
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0], val = arguments[1];"
                        + "const proto = el instanceof window.HTMLTextAreaElement"
                        + "    ? window.HTMLTextAreaElement.prototype"
                        + "    : window.HTMLInputElement.prototype;"
                        + "const setter = Object.getOwnPropertyDescriptor(proto, 'value').set;"
                        + "setter.call(el, val);"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));",
                element, text);
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
