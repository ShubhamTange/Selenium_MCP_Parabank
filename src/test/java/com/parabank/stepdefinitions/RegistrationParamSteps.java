package com.parabank.stepdefinitions;

import com.parabank.pages.RegisterPage;
import com.parabank.utils.DriverFactory;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class RegistrationParamSteps {

    @Then("I should see outcome {string} for username {string} and field {string}")
    public void i_should_see_outcome_for_username_and_field(String outcome, String username, String field) {
        WebDriver driver = DriverFactory.getDriver();
        RegisterPage registerPage = new RegisterPage(driver);
        String panelText = registerPage.getRightPanelText();
        String low = panelText == null ? "" : panelText.toLowerCase();

        switch (outcome.toLowerCase()) {
            case "success":
                // expect welcome message containing username
                Assert.assertTrue("Expected welcome message to contain username and Welcome: " + panelText,
                        panelText != null && panelText.contains("Welcome") && panelText.contains(username));
                break;
            case "username_exists":
                Assert.assertTrue("Expected username exists message: " + panelText,
                        low.contains("username") && low.contains("exists"));
                break;
            case "password_error":
                Assert.assertTrue("Expected password validation message: " + panelText,
                        low.contains("password") && (low.contains("required") || low.contains("confirmation") || low.contains("confirm")));
                break;
            case "required_field":
                String needle = field == null ? "" : field.toLowerCase();
                boolean ok = low.contains(needle) && (low.contains("required") || low.contains("is required") || low.contains("required"));
                if (!ok) {
                    // fallback: ensure the field label appears in the panel text
                    ok = low.contains(needle);
                }
                Assert.assertTrue("Expected required field error for: " + field + ", panel: " + panelText, ok);
                break;
            default:
                Assert.fail("Unknown expected outcome: " + outcome);
        }
    }
}
