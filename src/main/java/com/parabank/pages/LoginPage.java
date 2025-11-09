package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    private By username = By.name("username");
    private By password = By.name("password");
    private By loginButton = By.xpath("//input[@value='Log In' or @type='submit']");
    private By rightPanel = By.id("rightPanel");

    public void setUsername(String user) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(username));
        el.clear();
        el.sendKeys(user);
    }

    public void setPassword(String pass) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(password));
        el.clear();
        el.sendKeys(pass);
    }

    public void clickLogin() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            btn.click();
        } catch (Exception e) {
            // fallback: try direct click
            driver.findElement(loginButton).click();
        }
    }

    public String getRightPanelText() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement panel = wait.until(ExpectedConditions.visibilityOfElementLocated(rightPanel));
            return panel.getText().trim();
        } catch (Exception e) {
            try {
                return driver.findElement(rightPanel).getText().trim();
            } catch (Exception ex) {
                return "";
            }
        }
    }
}
