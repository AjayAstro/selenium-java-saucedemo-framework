package com.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Page Object for the final "checkout complete" confirmation page. */
public class CheckoutCompletePage extends BasePage {

    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");
    private static final By COMPLETE_TEXT   = By.cssSelector("[data-test='complete-text']");
    private static final By BACK_HOME       = By.id("back-to-products");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    @Step("Wait for the order confirmation to load")
    public CheckoutCompletePage waitUntilLoaded() {
        waitForUrlContains("checkout-complete.html");
        waitForVisible(COMPLETE_HEADER);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains("checkout-complete.html");
    }

    public String getConfirmationHeader() {
        return getText(COMPLETE_HEADER);
    }

    public String getConfirmationText() {
        return getText(COMPLETE_TEXT);
    }

    @Step("Return to the products page")
    public InventoryPage backToProducts() {
        click(BACK_HOME);
        return new InventoryPage(driver);
    }
}
