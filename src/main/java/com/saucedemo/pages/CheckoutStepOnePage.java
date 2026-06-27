package com.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Page Object for checkout step one: the customer-information form. */
public class CheckoutStepOnePage extends BasePage {

    private static final By FIRST_NAME    = By.id("first-name");
    private static final By LAST_NAME     = By.id("last-name");
    private static final By POSTAL_CODE   = By.id("postal-code");
    private static final By CONTINUE_BTN  = By.id("continue");
    private static final By CANCEL_BTN    = By.id("cancel");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public CheckoutStepOnePage(WebDriver driver) {
        super(driver);
    }

    @Step("Wait for checkout information page to load")
    public CheckoutStepOnePage waitUntilLoaded() {
        waitForUrlContains("checkout-step-one.html");
        waitForVisible(FIRST_NAME);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains("checkout-step-one.html");
    }

    @Step("Enter checkout information")
    public CheckoutStepOnePage enterCustomerInfo(String firstName, String lastName, String postalCode) {
        if (firstName != null) {
            type(FIRST_NAME, firstName);
        }
        if (lastName != null) {
            type(LAST_NAME, lastName);
        }
        if (postalCode != null) {
            type(POSTAL_CODE, postalCode);
        }
        return this;
    }

    @Step("Continue to the order overview")
    public CheckoutStepTwoPage continueToOverview() {
        click(CONTINUE_BTN);
        return new CheckoutStepTwoPage(driver);
    }

    /** Clicks continue when an error is expected, keeping us on this page. */
    @Step("Continue expecting a validation error")
    public CheckoutStepOnePage continueExpectingError() {
        click(CONTINUE_BTN);
        return this;
    }

    @Step("Cancel checkout")
    public CartPage cancel() {
        click(CANCEL_BTN);
        return new CartPage(driver);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }
}
