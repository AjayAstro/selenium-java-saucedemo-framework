package com.saucedemo.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * The header / burger-menu present on every authenticated SauceDemo page.
 *
 * <p>Encapsulates the cart link, the cart badge, and the slide-out menu actions
 * (logout, reset app state, open/close) so they are defined once and reused.
 */
public class HeaderComponent extends BasePage {

    private static final By BURGER_MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By CLOSE_MENU_BUTTON  = By.id("react-burger-cross-btn");
    private static final By LOGOUT_LINK        = By.id("logout_sidebar_link");
    private static final By RESET_LINK         = By.id("reset_sidebar_link");
    private static final By INVENTORY_LINK     = By.id("inventory_sidebar_link");
    private static final By MENU_WRAP          = By.className("bm-menu-wrap");
    private static final By CART_LINK          = By.className("shopping_cart_link");
    private static final By CART_BADGE         = By.className("shopping_cart_badge");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    @Step("Open the navigation menu")
    public HeaderComponent openMenu() {
        click(BURGER_MENU_BUTTON);
        // Menu is open once the wrapper is no longer aria-hidden.
        wait.until(ExpectedConditions.attributeToBe(MENU_WRAP, "aria-hidden", "false"));
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGOUT_LINK));
        return this;
    }

    @Step("Close the navigation menu")
    public HeaderComponent closeMenu() {
        // JS-click avoids missing the cross button while the menu is animating.
        jsClick(CLOSE_MENU_BUTTON);
        // The menu wrapper is hidden (aria-hidden=true) once the slide-out finishes.
        wait.until(ExpectedConditions.attributeToBe(MENU_WRAP, "aria-hidden", "true"));
        return this;
    }

    public boolean isMenuOpen() {
        String hidden = driver.findElement(MENU_WRAP).getDomAttribute("aria-hidden");
        // aria-hidden is "false" (or absent) while the menu is open.
        return hidden == null || hidden.equals("false");
    }

    @Step("Log out")
    public LoginPage logout() {
        openMenu();
        // JS-click the link to avoid the moving-target problem during the slide.
        jsClick(LOGOUT_LINK);
        // Logout is complete once the login form is back.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("login-button")));
        return new LoginPage(driver);
    }

    @Step("Reset application state")
    public HeaderComponent resetAppState() {
        openMenu();
        jsClick(RESET_LINK);
        closeMenu();
        return this;
    }

    @Step("Open the cart")
    public CartPage openCart() {
        // JS-click the anchor itself: once a badge is shown it overlaps the link
        // centre, and a native click can land on the badge span instead of the
        // anchor, so navigation never fires.
        jsClick(CART_LINK);
        wait.until(ExpectedConditions.urlContains("cart.html"));
        return new CartPage(driver);
    }

    /** @return the number on the cart badge, or 0 when no badge is shown. */
    public int getCartBadgeCount() {
        if (countOf(CART_BADGE) == 0) {
            return 0;
        }
        return Integer.parseInt(getText(CART_BADGE).trim());
    }

    public boolean isCartBadgeVisible() {
        return countOf(CART_BADGE) > 0;
    }
}
