package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Epic("Shopping")
@Feature("Cart")
public class CartTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void login() {
        inventory = loginPage.loginAsStandardUser().waitUntilLoaded();
    }

    @Test(description = "Add a single item to the cart")
    @Story("Add to cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addSingleItem() {
        inventory.addProductToCart(Products.BACKPACK);

        assertEquals(inventory.header().getCartBadgeCount(), 1, "Badge should show 1 item");

        CartPage cart = inventory.header().openCart().waitUntilLoaded();
        assertEquals(cart.getItemCount(), 1);
        assertTrue(cart.containsItem(Products.BACKPACK));
    }

    @Test(description = "Add multiple items to the cart")
    @Story("Add to cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addMultipleItems() {
        inventory.addProductToCart(Products.BACKPACK)
                .addProductToCart(Products.BIKE_LIGHT)
                .addProductToCart(Products.BOLT_TSHIRT);

        assertEquals(inventory.header().getCartBadgeCount(), 3, "Badge should show 3 items");

        CartPage cart = inventory.header().openCart().waitUntilLoaded();
        assertEquals(cart.getItemCount(), 3);
        assertTrue(cart.getItemNames().containsAll(
                java.util.List.of(Products.BACKPACK, Products.BIKE_LIGHT, Products.BOLT_TSHIRT)));
    }

    @Test(description = "Remove an item from the cart")
    @Story("Remove from cart")
    @Severity(SeverityLevel.NORMAL)
    public void removeItemFromCart() {
        inventory.addProductToCart(Products.BACKPACK).addProductToCart(Products.BIKE_LIGHT);

        CartPage cart = inventory.header().openCart().waitUntilLoaded();
        cart.removeItem(Products.BACKPACK);

        assertEquals(cart.getItemCount(), 1);
        assertFalse(cart.containsItem(Products.BACKPACK), "Backpack should have been removed");
        assertTrue(cart.containsItem(Products.BIKE_LIGHT));
    }

    @Test(description = "Cart badge updates as items are added and removed")
    @Story("Cart badge")
    @Severity(SeverityLevel.NORMAL)
    @Description("The badge should appear, increment, and disappear when the cart empties.")
    public void cartBadgeUpdates() {
        assertFalse(inventory.header().isCartBadgeVisible(), "No badge before adding items");

        inventory.addProductToCart(Products.BACKPACK);
        assertEquals(inventory.header().getCartBadgeCount(), 1);

        inventory.addProductToCart(Products.BIKE_LIGHT);
        assertEquals(inventory.header().getCartBadgeCount(), 2);

        inventory.removeProductFromCart(Products.BACKPACK);
        assertEquals(inventory.header().getCartBadgeCount(), 1);
    }

    @Test(description = "Selected items persist when navigating back to the inventory")
    @Story("Cart persistence")
    @Severity(SeverityLevel.NORMAL)
    public void cartPersistsAcrossNavigation() {
        inventory.addProductToCart(Products.BACKPACK).addProductToCart(Products.FLEECE_JACKET);

        CartPage cart = inventory.header().openCart().waitUntilLoaded();
        cart.continueShopping().waitUntilLoaded();

        // Re-open the cart and confirm the selection survived the round trip.
        CartPage reopened = inventory.header().openCart().waitUntilLoaded();
        assertEquals(reopened.getItemCount(), 2, "Both items should still be in the cart");
        assertTrue(reopened.containsItem(Products.BACKPACK));
        assertTrue(reopened.containsItem(Products.FLEECE_JACKET));
    }
}
