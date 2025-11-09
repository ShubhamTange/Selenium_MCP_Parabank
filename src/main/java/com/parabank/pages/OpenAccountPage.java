package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class OpenAccountPage {
    private final WebDriver driver;

    public OpenAccountPage(WebDriver driver) {
        this.driver = driver;
    }

    private By openAccountLink = By.xpath("//a[text()='Open New Account']");
    private By accountTypeSelect = By.id("type");
    //private By fromAccountSelect = By.id("fromAccountId");
    private By openAccountButton = By.xpath("//input[@value='Open New Account' and @type='button']");
    //private By rightPanel = By.id("rightPanel");
    private By rightPanel = By.xpath("//div[@id='openAccountResult']/h1");

    public void navigateToOpenAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(openAccountLink));
        link.click();
    }

    public void selectAccountType(String type) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement sel = wait.until(ExpectedConditions.visibilityOfElementLocated(accountTypeSelect));
        Select s = new Select(sel);
        // try to select by visible text or value
        try {
            s.selectByVisibleText(type);
        } catch (Exception e) {
            s.selectByValue(type);
        }
    }

    public void selectFromAccount(String account) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        WebElement sel = findFromAccountSelectElement(wait);
        Select s = new Select(sel);
        // Wait for option elements to appear (some pages populate the options asynchronously)
        try {
            WebDriverWait waitForOptions = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitForOptions.until(d -> {
                java.util.List<WebElement> opts = sel.findElements(org.openqa.selenium.By.tagName("option"));
                return opts != null && opts.size() > 0;
            });
        } catch (Exception ignored) {
            // timed out waiting for options - we'll dump whatever (possibly zero) options exist
        }
        // Prefer matching by visible text or by option value by iterating options to avoid exceptions
        java.util.List<WebElement> options = s.getOptions();
        // Dump available options for debugging so test logs/show the actual DOM state
        dumpFromAccountOptions(options);
        boolean matched = false;
        for (int i = 0; i < options.size(); i++) {
            WebElement opt = options.get(i);
            String text = opt.getText();
            String val = opt.getAttribute("value");
            if (text != null && text.trim().equals(account)) {
                s.selectByIndex(i);
                matched = true;
                break;
            }
            if (val != null && val.equals(account)) {
                s.selectByIndex(i);
                matched = true;
                break;
            }
        }
        if (!matched) {
            // fallback: choose the first real option (skip placeholder if present)
            int size = options.size();
            if (size > 1) {
                s.selectByIndex(1);
            } else if (size == 1) {
                s.selectByIndex(0);
            } else {
                throw new org.openqa.selenium.NoSuchElementException("No options available for fromAccount select. Check logs: target/logs/fromAccountOptions.txt");
            }
        }
    }

    /**
     * Try multiple locators to find the from-account select element. This helps when the ID/name
     * changes or the element is inside a different parent structure.
     */
    private WebElement findFromAccountSelectElement(WebDriverWait wait) {
        By[] candidates = new By[] {
            By.id("fromAccountId"),
            By.name("fromAccountId"),
            By.cssSelector("select[name='fromAccountId']"),
            By.xpath("//label[contains(., 'From Account')]/following::select[1]"),
            By.xpath("//select[@id='fromAccountId' or contains(@name,'fromAccount')]")
        };
        for (By b : candidates) {
            try {
                WebElement e = wait.until(ExpectedConditions.visibilityOfElementLocated(b));
                if (e != null) return e;
            } catch (Exception ignored) {
            }
        }
        // Last resort: try to find any select on the page that looks like it belongs to account selection
        try {
            java.util.List<WebElement> selects = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.tagName("select")));
            if (selects.size() > 0) return selects.get(0);
        } catch (Exception ignored) {
        }
        throw new org.openqa.selenium.NoSuchElementException("Could not locate the from-account select element using known locators");
    }

    private void dumpFromAccountOptions(java.util.List<WebElement> options) {
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(options.size()).append(" options for fromAccount select:\n");
        for (WebElement opt : options) {
            String text = opt.getText();
            String val = opt.getAttribute("value");
            sb.append("- text='").append(text).append("' value='").append(val).append("'\n");
        }
        String out = sb.toString();
        System.out.println(out);
        // write to target/logs so it's easy to inspect after a test run
        try {
            java.nio.file.Path logsDir = java.nio.file.Paths.get("target", "logs");
            java.nio.file.Files.createDirectories(logsDir);
            java.nio.file.Files.write(logsDir.resolve("fromAccountOptions.txt"), out.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public void clickOpenAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(openAccountButton));
        btn.click();
    }

    public String getRightPanelText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement panel = wait.until(ExpectedConditions.visibilityOfElementLocated(rightPanel));
        return panel.getText().trim();
    }
}
