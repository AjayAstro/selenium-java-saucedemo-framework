package com.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Comparator;
import java.util.List;

/** Page Object for the inventory (products) page shown after a successful login. */
public class InventoryPage extends BasePage {

    private static final By INVENTORY_CONTAINER = By.id("inventory_container");
    private static final By INVENTORY_ITEM      = By.className("inventory_item");
    private static final By ITEM_NAME           = By.className("inventory_item_name");
    private static final By ITEM_PRICE          = By.className("inventory_item_price");
    private static final By SORT_DROPDOWN       = By.className("product_sort_container");
    private static final By PAGE_TITLE          = By.className("title");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    /** Sort options exposed by SauceDemo, paired with their {@code value} attribute. */
    public enum SortOption {
        NAME_A_TO_Z("az"),
        NAME_Z_TO_A("za"),
        PRICE_LOW_TO_HIGH("lohi"),
        PRICE_HIGH_TO_LOW("hilo");

        private final String value;

        SortOption(String value) {
            this.value = value;
        }
    }

    @Step("Wait for the inventory page to load")
    public InventoryPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        waitForVisible(INVENTORY_CONTAINER);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains("inventory.html") && isDisplayed(INVENTORY_CONTAINER);
    }

    public String getTitle() {
        return getText(PAGE_TITLE);
    }

    public int getProductCount() {
        return countOf(INVENTORY_ITEM);
    }

    public boolean isProductListVisible() {
        return isDisplayed(INVENTORY_CONTAINER) && getProductCount() > 0;
    }

    public List<String> getProductNames() {
        return findAll(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public List<Double> getProductPrices() {
        return findAll(ITEM_PRICE).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "").trim()))
                .toList();
    }

    @Step("Sort products by {option}")
    public InventoryPage sortBy(SortOption option) {
        new Select(waitForVisible(SORT_DROPDOWN)).selectByValue(option.value);
        return this;
    }

    @Step("Add product to cart: {productName}")
    public InventoryPage addProductToCart(String productName) {
        clickActionButton(productName);
        // Confirm the action registered: the button toggles to "Remove".
        waitForButtonState(productName, "remove");
        return this;
    }

    @Step("Remove product from cart: {productName}")
    public InventoryPage removeProductFromCart(String productName) {
        clickActionButton(productName);
        // Confirm the action registered: the button toggles back to "Add to cart".
        waitForButtonState(productName, "add-to-cart");
        return this;
    }

    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }

    /**
     * Clicks the Add/Remove button on the card for {@code productName}. Locating
     * relative to the product card keeps the action robust regardless of the
     * current sort order or the button's add/remove state.
     */
    private void clickActionButton(String productName) {
        click(actionButtonLocator(productName));
    }

    private By actionButtonLocator(String productName) {
        return By.xpath(
                "//div[contains(@class,'inventory_item')]"
                        + "[.//*[@data-test='inventory-item-name' and normalize-space()=\"" + productName + "\"]]"
                        + "//button");
    }

    /**
     * Waits until the card's button reflects the expected state, identified by
     * the {@code data-test} prefix ("add-to-cart" or "remove"). This is the
     * reliable signal that the click was processed by the app.
     */
    private void waitForButtonState(String productName, String dataTestPrefix) {
        wait.until(d -> {
            var buttons = d.findElements(actionButtonLocator(productName));
            if (buttons.isEmpty()) {
                return false;
            }
            String dataTest = buttons.get(0).getDomAttribute("data-test");
            return dataTest != null && dataTest.startsWith(dataTestPrefix);
        });
    }

    /** Convenience helper used by sorting tests to verify ordering. */
    public boolean isSortedByNameAscending() {
        List<String> names = getProductNames();
        return names.equals(names.stream().sorted().toList());
    }

    public boolean isSortedByNameDescending() {
        List<String> names = getProductNames();
        return names.equals(names.stream().sorted(Comparator.reverseOrder()).toList());
    }

    public boolean isSortedByPriceAscending() {
        List<Double> prices = getProductPrices();
        return prices.equals(prices.stream().sorted().toList());
    }

    public boolean isSortedByPriceDescending() {
        List<Double> prices = getProductPrices();
        return prices.equals(prices.stream().sorted(Comparator.reverseOrder()).toList());
    }
}
