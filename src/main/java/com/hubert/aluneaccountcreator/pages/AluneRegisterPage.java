package com.hubert.aluneaccountcreator.pages;

import com.hubert.aluneaccountcreator.models.UserRegisterCredentials;
import com.hubert.aluneaccountcreator.models.UserRegisterResponse;
import com.hubert.aluneaccountcreator.utils.ThreadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
public class AluneRegisterPage {
    private final WebDriver webDriver;

    public void get() {
        webDriver.get("https://alune.pl/register.html");
    }

    public UserRegisterResponse createAccount(UserRegisterCredentials credentials, boolean isWithRandomDelays) {
        webDriver.findElement(By.cssSelector("#modal-center")).click();

        List<WebElement> inputs = webDriver.findElements(By.cssSelector(".page .post-content input"));
        WebElement login = inputs.get(0);
        WebElement email = inputs.get(1);
        WebElement password = inputs.get(2);
        WebElement passwordConfirmation = inputs.get(3);
        WebElement deleteCharacterCode = inputs.get(4);
        WebElement acceptRules = webDriver.findElement(By.cssSelector("input[type=checkbox]"));

        login.sendKeys(credentials.login());
        randomDelayIfNeeded(isWithRandomDelays, 300, 2000);
        email.sendKeys(credentials.email());
        randomDelayIfNeeded(isWithRandomDelays, 300, 2000);
        password.sendKeys(credentials.password());
        randomDelayIfNeeded(isWithRandomDelays, 300, 2000);
        passwordConfirmation.sendKeys(credentials.password());
        randomDelayIfNeeded(isWithRandomDelays, 300, 2000);
        deleteCharacterCode.sendKeys(credentials.socialId());
        randomDelayIfNeeded(isWithRandomDelays, 300, 2000);
        acceptRules.click();

        WebElement submitButton = webDriver.findElement(By.cssSelector("div[class='post-content'] button[type=submit]"));

        submitButton.submit();

        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofMillis(5000));
        By pinSelector = By.cssSelector(".page .post-content .badge");

        try {
            webDriverWait.until((driver) -> pinSelector.findElement(driver) != null);
        } catch (Exception ignored) {
            log.error("Account already created. {}", credentials);
            return null;
        }

        WebElement pin = webDriver.findElement(pinSelector);

        return new UserRegisterResponse(credentials.login(), true, pin.getAttribute("textContent"));
    }

    private void randomDelayIfNeeded(boolean isNeeded, long startMs, long endMs) {
        if (isNeeded) {
            long delay = ThreadLocalRandom.current().nextLong(startMs, endMs);

            ThreadUtils.delay(delay);
        }
    }
}
