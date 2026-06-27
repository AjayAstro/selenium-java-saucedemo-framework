package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutStepOnePage;
import com.saucedemo.pages.CheckoutStepTwoPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.testdata.Messages;
import com.saucedemo.testdata.Products;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("Shopping")
@Feature("Checkout")
public class CheckoutTest extends BaseTest {

    private static final double PRICE_TOLERANCE = 0.001;

    private CartPage cart;

    @BeforeMethod(alwaysRun = true)
    public void loginAndFillCart() {
        InventoryPage inventory = loginPage.loginAsStandardUser().waitUntilLoaded();
        inventory.addProductToCart(Products.BACKPACK).addProductToCart(Products.BIKE_LIGHT);
        cart = inventory.header().openCart().waitUntilLoaded();
    }

    @Test(description = "Complete the full checkout flow successfully")
    @Story("Successful checkout")
    @Severity(SeverityLevel.BLOCKER)
    @Description("From a populated cart, a customer can complete checkout end to end.")
    public void successfulCheckout() {
        CheckoutCompletePage complete = cart.checkout()
                .waitUntilLoaded()
                .enterCustomerInfo("Ada", "Lovelace", "12345")
                .continueToOverview()
                .waitUntilLoaded()
                .finish()
                .waitUntilLoaded();

        assertTrue(complete.isLoaded(), "Should land on the confirmation page");
        assertEquals(complete.getConfirmationHeader(), Messages.ORDER_COMPLETE_HEADER);
    }

    @Test(description = "Order completion message is displayed")
    @Story("Order confirmation")
    @Severity(SeverityLevel.CRITICAL)
    public void orderCompletionMessageIsShown() {
        CheckoutCompletePage complete = cart.checkout()
                .waitUntilLoaded()
                .enterCustomerInfo("Grace", "Hopper", "99999")
                .continueToOverview()
                .waitUntilLoaded()
                .finish()
                .waitUntilLoaded();

        assertEquals(complete.getConfirmationHeader(), Messages.ORDER_COMPLETE_HEADER);
        assertTrue(complete.getConfirmationText().toLowerCase().contains("order"),
                "Confirmation text should reference the order");
    }

    @Test(description = "Missing first name is rejected")
    @Story("Field validation")
    @Severity(SeverityLevel.NORMAL)
    public void missingFirstNameValidation() {
        CheckoutStepOnePage stepOne = cart.checkout().waitUntilLoaded()
                .enterCustomerInfo(null, "Lovelace", "12345")
                .continueExpectingError();

        assertTrue(stepOne.isErrorDisplayed());
        assertEquals(stepOne.getErrorMessage(), Messages.FIRST_NAME_REQUIRED);
    }

    @Test(description = "Missing last name is rejected")
    @Story("Field validation")
    @Severity(SeverityLevel.NORMAL)
    public void missingLastNameValidation() {
        CheckoutStepOnePage stepOne = cart.checkout().waitUntilLoaded()
                .enterCustomerInfo("Ada", null, "12345")
                .continueExpectingError();

        assertTrue(stepOne.isErrorDisplayed());
        assertEquals(stepOne.getErrorMessage(), Messages.LAST_NAME_REQUIRED);
    }

    @Test(description = "Missing postal code is rejected")
    @Story("Field validation")
    @Severity(SeverityLevel.NORMAL)
    public void missingPostalCodeValidation() {
        CheckoutStepOnePage stepOne = cart.checkout().waitUntilLoaded()
                .enterCustomerInfo("Ada", "Lovelace", null)
                .continueExpectingError();

        assertTrue(stepOne.isErrorDisplayed());
        assertEquals(stepOne.getErrorMessage(), Messages.POSTAL_CODE_REQUIRED);
    }

    @Test(description = "Overview subtotal equals the sum of item prices")
    @Story("Order overview totals")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The displayed item total must match the sum of the line items, "
            + "and the grand total must equal subtotal + tax.")
    public void overviewTotalsAreConsistent() {
        CheckoutStepTwoPage overview = cart.checkout()
                .waitUntilLoaded()
                .enterCustomerInfo("Ada", "Lovelace", "12345")
                .continueToOverview()
                .waitUntilLoaded();

        assertEquals(overview.getItemCount(), 2, "Both items should appear on the overview");
        assertEquals(overview.getDisplayedSubtotal(), overview.getItemsTotal(), PRICE_TOLERANCE,
                "Displayed subtotal should equal the sum of item prices");
        assertEquals(overview.getDisplayedTotal(),
                overview.getDisplayedSubtotal() + overview.getTax(), PRICE_TOLERANCE,
                "Grand total should equal subtotal + tax");
    }
}
