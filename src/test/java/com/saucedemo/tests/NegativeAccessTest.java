package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.config.ConfigManager;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.testdata.Messages;
import com.saucedemo.testdata.Products;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("Security & validation")
@Feature("Negative scenarios")
public class NegativeAccessTest extends BaseTest {

    @Test(description = "Protected pages redirect unauthenticated users to login")
    @Story("Protected routes")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Hitting /inventory.html directly without a session must not expose the page.")
    public void protectedPageRedirectsWhenNotLoggedIn() {
        getDriver().get(ConfigManager.baseUrl() + "/inventory.html");

        LoginPage login = new LoginPage(getDriver());
        assertTrue(login.isLoaded(), "Should be bounced back to the login page");
        assertTrue(login.isErrorDisplayed(), "An access error should be displayed");
        assertTrue(login.getErrorMessage().toLowerCase().contains("you can only access"),
                "Error should explain the page requires authentication");
    }

    @Test(description = "Locked-out user cannot reach the inventory")
    @Story("Locked-out access")
    @Severity(SeverityLevel.CRITICAL)
    public void lockedOutUserCannotAccessInventory() {
        loginPage.loginExpectingFailure(ConfigManager.lockedOutUser(), ConfigManager.password());

        InventoryPage inventory = new InventoryPage(getDriver());
        assertFalse(inventory.isLoaded(), "Locked-out user must never reach the inventory");
        assertTrue(loginPage.getErrorMessage().equals(Messages.LOCKED_OUT_USER));
    }

    @Test(description = "Checkout cannot continue while required fields are empty")
    @Story("Checkout guardrails")
    @Severity(SeverityLevel.NORMAL)
    public void checkoutBlocksWhenFieldsMissing() {
        InventoryPage inventory = loginPage.loginAsStandardUser().waitUntilLoaded();
        inventory.addProductToCart(Products.BACKPACK);

        var stepOne = inventory.header().openCart().waitUntilLoaded()
                .checkout().waitUntilLoaded()
                .continueExpectingError();

        assertTrue(stepOne.isLoaded(), "Should remain on the information step");
        assertTrue(stepOne.isErrorDisplayed(), "A validation error should block progress");
    }
}
