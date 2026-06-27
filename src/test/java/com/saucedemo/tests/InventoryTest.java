package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.InventoryPage.SortOption;
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
@Feature("Inventory")
public class InventoryTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void login() {
        inventory = loginPage.loginAsStandardUser().waitUntilLoaded();
    }

    @Test(description = "Inventory page loads after login")
    @Story("Inventory loads")
    @Severity(SeverityLevel.BLOCKER)
    public void inventoryPageLoads() {
        assertTrue(inventory.isLoaded(), "Inventory page should be loaded");
        assertEquals(inventory.getTitle(), "Products");
    }

    @Test(description = "All six products are visible")
    @Story("Product list")
    @Severity(SeverityLevel.CRITICAL)
    @Description("SauceDemo ships six demo products; all should be rendered.")
    public void productListIsVisible() {
        assertTrue(inventory.isProductListVisible(), "Product list should be visible");
        assertEquals(inventory.getProductCount(), 6, "Expected six products");
    }

    @Test(description = "Sort products by name A to Z")
    @Story("Sorting")
    @Severity(SeverityLevel.NORMAL)
    public void sortByNameAtoZ() {
        inventory.sortBy(SortOption.NAME_A_TO_Z);
        assertTrue(inventory.isSortedByNameAscending(), "Products should be sorted A-Z");
    }

    @Test(description = "Sort products by name Z to A")
    @Story("Sorting")
    @Severity(SeverityLevel.NORMAL)
    public void sortByNameZtoA() {
        inventory.sortBy(SortOption.NAME_Z_TO_A);
        assertTrue(inventory.isSortedByNameDescending(), "Products should be sorted Z-A");
    }

    @Test(description = "Sort products by price low to high")
    @Story("Sorting")
    @Severity(SeverityLevel.NORMAL)
    public void sortByPriceLowToHigh() {
        inventory.sortBy(SortOption.PRICE_LOW_TO_HIGH);
        assertTrue(inventory.isSortedByPriceAscending(), "Products should be sorted by ascending price");
    }

    @Test(description = "Sort products by price high to low")
    @Story("Sorting")
    @Severity(SeverityLevel.NORMAL)
    public void sortByPriceHighToLow() {
        inventory.sortBy(SortOption.PRICE_HIGH_TO_LOW);
        assertTrue(inventory.isSortedByPriceDescending(), "Products should be sorted by descending price");
    }
}
