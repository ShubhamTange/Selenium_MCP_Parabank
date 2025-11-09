package com.parabank.stepdefinitions;

import com.parabank.pages.LoginPage;
import com.parabank.pages.RegisterPage;
import com.parabank.utils.DriverFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class LoginSteps {
    private WebDriver driver;
    private LoginPage loginPage;

    @Before
    public void setUp() {
        driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
    }

    @When("I attempt to login with username {string} and password {string}")
    public void i_attempt_to_login_with_username_and_password(String user, String pass) {
        // open home is handled by shared Given step
        // fill and submit
        loginPage.setUsername(user);
        loginPage.setPassword(pass);
        loginPage.clickLogin();
    }

    @io.cucumber.java.en.Given("user {string} exists with password {string}")
    public void user_exists_with_password(String user, String pass) {
        // create the user if possible via the registration page; if it already exists, the site will show an error but that's fine
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.clickRegisterLink();
        registerPage.setFirstName("Auto");
        registerPage.setLastName("User");
        registerPage.setAddress("1 Test Ave");
        registerPage.setCity("Testville");
        registerPage.setState("TS");
        registerPage.setZip("00000");
        registerPage.setPhone("000-0000");
        registerPage.setSsn("000-00-0000");
        registerPage.setUsername(user);
        registerPage.setPassword(pass);
        registerPage.setConfirm(pass);
        registerPage.submit();
        // no assertion here; caller will assert after login
    }

    @Then("I should see a successful login message for {string}")
    public void i_should_see_successful_login(String username) {
        String panel = loginPage.getRightPanelText();
        String low = panel == null ? "" : panel.toLowerCase();
        boolean ok = (panel != null && panel.contains(username)) || low.contains("welcome") || low.contains("accounts overview") || low.contains("account services");
        Assert.assertTrue("Expected successful-login indicator in panel: " + panel, ok);
    }

    @Then("I should see a login error mentioning invalid credentials")
    public void i_should_see_invalid_credentials_error() {
        String panel = loginPage.getRightPanelText().toLowerCase();
        Assert.assertTrue("Expected invalid credentials message", panel.contains("invalid") || panel.contains("username or password") || panel.contains("error"));
        saveScreenshot("error_invalid_credentials.png");
    }

    @Then("I should see a login validation error for {string}")
    public void i_should_see_login_validation_error_for(String field) {
        String panel = loginPage.getRightPanelText().toLowerCase();
        Assert.assertTrue("Expected validation error for " + field, panel.contains(field.toLowerCase()) || panel.contains("required"));
        saveScreenshot("error_login_validation_" + field.replaceAll("[^a-zA-Z0-9]","_") + ".png");
    }

    private void saveScreenshot(String fileName) {
        try {
            if (driver instanceof TakesScreenshot) {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Path screenshotsDir = Paths.get("target", "screenshots");
                if (!Files.exists(screenshotsDir)) {
                    Files.createDirectories(screenshotsDir);
                }
                Path out = screenshotsDir.resolve(fileName.replaceAll("[^a-zA-Z0-9._-]", "_"));
                Files.write(out, bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
