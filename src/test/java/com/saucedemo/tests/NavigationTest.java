package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.HeaderComponent;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.testdata.Products;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("Navigation")
@Feature("Menu & session")
public class NavigationTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void login() {
        inventory = loginPage.loginAsStandardUser().waitUntilLoaded();
    }

    @Test(description = "Logout returns the user to the login page")
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    public void logoutWorks() {
        LoginPage afterLogout = inventory.header().logout();

        assertTrue(afterLogout.isLoaded(), "Should be back on the login page");
        assertTrue(afterLogout.getCurrentUrl().endsWith("saucedemo.com/"),
                "URL should return to the application root");
    }

    @Test(description = "Reset app state clears the cart")
    @Story("Reset app state")
    @Severity(SeverityLevel.NORMAL)
    public void resetAppStateClearsCart() {
        inventory.addProductToCart(Products.BACKPACK).addProductToCart(Products.BIKE_LIGHT);
        assertEquals(inventory.header().getCartBadgeCount(), 2);

        inventory.header().resetAppState();

        assertFalse(inventory.header().isCartBadgeVisible(), "Cart badge should be gone after reset");
    }

    @Test(description = "The burger menu opens and closes")
    @Story("Menu open/close")
    @Severity(SeverityLevel.MINOR)
    public void menuOpensAndCloses() {
        HeaderComponent header = inventory.header();

        header.openMenu();
        assertTrue(header.isMenuOpen(), "Menu should be open");

        header.closeMenu();
        assertFalse(header.isMenuOpen(), "Menu should be closed");
    }
}
