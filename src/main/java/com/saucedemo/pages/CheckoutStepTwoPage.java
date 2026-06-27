package com.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Page Object for checkout step two: the order overview / summary. */
public class CheckoutStepTwoPage extends BasePage {

    private static final By CART_ITEM      = By.className("cart_item");
    private static final By ITEM_NAME      = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICE     = By.cssSelector("[data-test='inventory-item-price']");
    private static final By SUBTOTAL_LABEL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX_LABEL      = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL_LABEL    = By.cssSelector("[data-test='total-label']");
    private static final By FINISH_BUTTON  = By.id("finish");
    private static final By CANCEL_BUTTON  = By.id("cancel");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
    }

    @Step("Wait for the order overview to load")
    public CheckoutStepTwoPage waitUntilLoaded() {
        waitForUrlContains("checkout-step-two.html");
        waitForVisible(FINISH_BUTTON);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains("checkout-step-two.html");
    }

    public List<String> getItemNames() {
        return findAll(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public int getItemCount() {
        return countOf(CART_ITEM);
    }

    /** Sum of the individual line-item prices on the overview. */
    public double getItemsTotal() {
        return findAll(ITEM_PRICE).stream()
                .mapToDouble(e -> parseMoney(e.getText()))
                .sum();
    }

    /** "Item total: $39.98" -> 39.98 */
    public double getDisplayedSubtotal() {
        return parseMoney(getText(SUBTOTAL_LABEL));
    }

    /** "Tax: $3.20" -> 3.20 */
    public double getTax() {
        return parseMoney(getText(TAX_LABEL));
    }

    /** "Total: $43.18" -> 43.18 */
    public double getDisplayedTotal() {
        return parseMoney(getText(TOTAL_LABEL));
    }

    @Step("Finish the order")
    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver);
    }

    @Step("Cancel the order")
    public InventoryPage cancel() {
        click(CANCEL_BUTTON);
        return new InventoryPage(driver);
    }

    /** Extracts the numeric amount from any label that ends with "$<amount>". */
    private static double parseMoney(String text) {
        int dollarIndex = text.indexOf('$');
        String number = (dollarIndex >= 0) ? text.substring(dollarIndex + 1) : text;
        return Double.parseDouble(number.trim());
    }
}
