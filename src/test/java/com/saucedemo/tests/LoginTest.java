package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.config.ConfigManager;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.testdata.Messages;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(description = "Standard user can log in successfully")
    @Story("Successful login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("A valid standard_user should be authenticated and land on the inventory page.")
    public void standardUserCanLogIn() {
        InventoryPage inventory = loginPage.loginAsStandardUser().waitUntilLoaded();

        assertTrue(inventory.isLoaded(), "Inventory page should load after a successful login");
        assertEquals(inventory.getTitle(), "Products", "Inventory page title should be 'Products'");
    }

    @Test(description = "Locked-out user is rejected with a clear message")
    @Story("Locked-out user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("locked_out_user must not be able to log in and should see the lock-out error.")
    public void lockedOutUserIsRejected() {
        loginPage.loginExpectingFailure(ConfigManager.lockedOutUser(), ConfigManager.password());

        assertTrue(loginPage.isErrorDisplayed(), "An error banner should be shown");
        assertEquals(loginPage.getErrorMessage(), Messages.LOCKED_OUT_USER);
    }

    @Test(description = "Invalid username/password is rejected")
    @Story("Invalid credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidCredentialsAreRejected() {
        loginPage.loginExpectingFailure("invalid_user", "wrong_password");

        assertTrue(loginPage.isErrorDisplayed());
        assertEquals(loginPage.getErrorMessage(), Messages.INVALID_CREDENTIALS);
    }

    @Test(description = "Missing username shows the required-field error")
    @Story("Field validation")
    @Severity(SeverityLevel.NORMAL)
    public void missingUsernameShowsError() {
        loginPage.loginExpectingFailure(null, ConfigManager.password());

        assertTrue(loginPage.isErrorDisplayed());
        assertEquals(loginPage.getErrorMessage(), Messages.USERNAME_REQUIRED);
    }

    @Test(description = "Missing password shows the required-field error")
    @Story("Field validation")
    @Severity(SeverityLevel.NORMAL)
    public void missingPasswordShowsError() {
        loginPage.loginExpectingFailure(ConfigManager.standardUser(), null);

        assertTrue(loginPage.isErrorDisplayed());
        assertEquals(loginPage.getErrorMessage(), Messages.PASSWORD_REQUIRED);
    }
}
