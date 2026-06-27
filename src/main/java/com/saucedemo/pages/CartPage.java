package com.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Page Object for the shopping cart page. */
public class CartPage extends BasePage {

    private static final By CART_LIST         = By.className("cart_list");
    private static final By CART_ITEM         = By.className("cart_item");
    private static final By ITEM_NAME         = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CHECKOUT_BUTTON   = By.id("checkout");
    private static final By CONTINUE_SHOPPING = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Wait for the cart page to load")
    public CartPage waitUntilLoaded() {
        waitForUrlContains("cart.html");
        waitForVisible(CART_LIST);
        return this;
    }

    public boolean isLoaded() {
        return getCurrentUrl().contains("cart.html") && isDisplayed(CART_LIST);
    }

    public int getItemCount() {
        return countOf(CART_ITEM);
    }

    public List<String> getItemNames() {
        return findAll(ITEM_NAME).stream().map(WebElement::getText).toList();
    }

    public boolean containsItem(String productName) {
        return getItemNames().contains(productName);
    }

    @Step("Remove product from cart: {productName}")
    public CartPage removeItem(String productName) {
        By removeButton = By.xpath(
                "//div[contains(@class,'cart_item')]"
                        + "[.//*[@data-test='inventory-item-name' and normalize-space()=\"" + productName + "\"]]"
                        + "//button");
        click(removeButton);
        return this;
    }

    @Step("Proceed to checkout")
    public CheckoutStepOnePage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutStepOnePage(driver);
    }

    @Step("Continue shopping")
    public InventoryPage continueShopping() {
        click(CONTINUE_SHOPPING);
        return new InventoryPage(driver);
    }

    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }
}
