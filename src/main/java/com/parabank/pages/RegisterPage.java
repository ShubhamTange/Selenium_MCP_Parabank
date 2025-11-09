package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RegisterPage {
    private final WebDriver driver;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }

    // Locators (names observed on the page)
    private By registerLink = By.xpath("//a[text()='Register']");
    private By firstName = By.name("customer.firstName");
    private By lastName = By.name("customer.lastName");
    private By address = By.name("customer.address.street");
    private By city = By.name("customer.address.city");
    private By state = By.name("customer.address.state");
    private By zip = By.name("customer.address.zipCode");
    private By phone = By.name("customer.phoneNumber");
    private By ssn = By.name("customer.ssn");
    private By username = By.name("customer.username");
    private By password = By.name("customer.password");
    private By confirm = By.name("repeatedPassword");
    private By registerButton = By.xpath("//input[@value='Register' and @type='submit']");
    private By rightPanel = By.id("rightPanel");

    public void clickRegisterLink() {
        // Try to click the Register link; if it's not present, navigate to home and retry.
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(registerLink));
            el.click();
        } catch (TimeoutException e) {
            // If not found/clickable, navigate to the home page and try again
            driver.get("https://parabank.parasoft.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(registerLink));
            el.click();
        }
    }

    public void setFirstName(String value) { driver.findElement(firstName).clear(); driver.findElement(firstName).sendKeys(value); }
    public void setLastName(String value) { driver.findElement(lastName).clear(); driver.findElement(lastName).sendKeys(value); }
    public void setAddress(String value) { driver.findElement(address).clear(); driver.findElement(address).sendKeys(value); }
    public void setCity(String value) { driver.findElement(city).clear(); driver.findElement(city).sendKeys(value); }
    public void setState(String value) { driver.findElement(state).clear(); driver.findElement(state).sendKeys(value); }
    public void setZip(String value) { driver.findElement(zip).clear(); driver.findElement(zip).sendKeys(value); }
    public void setPhone(String value) { driver.findElement(phone).clear(); driver.findElement(phone).sendKeys(value); }
    public void setSsn(String value) { driver.findElement(ssn).clear(); driver.findElement(ssn).sendKeys(value); }
    public void setUsername(String value) { driver.findElement(username).clear(); driver.findElement(username).sendKeys(value); }
    public void setPassword(String value) { driver.findElement(password).clear(); driver.findElement(password).sendKeys(value); }
    public void setConfirm(String value) { driver.findElement(confirm).clear(); driver.findElement(confirm).sendKeys(value); }

    public void submit() { driver.findElement(registerButton).click(); }

    public String getRightPanelText() {
        WebElement panel = driver.findElement(rightPanel);
        return panel.getText().trim();
    }
}
