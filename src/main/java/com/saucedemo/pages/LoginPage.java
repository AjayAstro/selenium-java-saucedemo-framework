package com.saucedemo.pages;

import com.saucedemo.config.ConfigManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Page Object for the SauceDemo login page (the application entry point). */
public class LoginPage extends BasePage {

    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON   = By.id("login-button");
    private static final By ERROR_MESSAGE  = By.cssSelector("[data-test='error']");
    private static final By LOGIN_LOGO     = By.className("login_logo");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the SauceDemo login page")
    public LoginPage open() {
        driver.get(ConfigManager.baseUrl());
        waitForVisible(LOGIN_LOGO);
        return this;
    }

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(USERNAME_INPUT, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    @Step("Click the login button")
    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    /** Fills both fields and submits, returning the inventory page on success. */
    @Step("Log in as {username}")
    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new InventoryPage(driver);
    }

    /** Logs in with the configured standard user. */
    public InventoryPage loginAsStandardUser() {
        return loginAs(ConfigManager.standardUser(), ConfigManager.password());
    }

    /**
     * Submits credentials that are expected to fail, leaving the user on the
     * login page so the error banner can be asserted.
     */
    @Step("Attempt login as {username} (expecting failure)")
    public LoginPage loginExpectingFailure(String username, String password) {
        if (username != null) {
            enterUsername(username);
        }
        if (password != null) {
            enterPassword(password);
        }
        clickLogin();
        return this;
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }

    public boolean isLoaded() {
        return isDisplayed(LOGIN_BUTTON);
    }
}
