package com.saucedemo.bdd.steps;

import com.saucedemo.driver.DriverManager;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Step definitions backing {@code login.feature}; they delegate to the Page Objects. */
public class LoginSteps {

    private LoginPage loginPage;

    @Given("the login page is open")
    public void theLoginPageIsOpen() {
        loginPage = new LoginPage(DriverManager.getDriver()).open();
    }

    @When("I log in as the standard user")
    public void iLogInAsTheStandardUser() {
        loginPage.loginAsStandardUser();
    }

    @When("I log in as {string} with password {string}")
    public void iLogInAs(String username, String password) {
        loginPage.loginExpectingFailure(username, password);
    }

    @Then("I should land on the inventory page")
    public void iShouldLandOnTheInventoryPage() {
        InventoryPage inventory = new InventoryPage(DriverManager.getDriver()).waitUntilLoaded();
        assertTrue(inventory.isLoaded(), "Inventory page should be loaded");
    }

    @Then("I should see the login error {string}")
    public void iShouldSeeTheLoginError(String expected) {
        assertTrue(loginPage.isErrorDisplayed(), "An error banner should be shown");
        assertEquals(loginPage.getErrorMessage(), expected);
    }
}
