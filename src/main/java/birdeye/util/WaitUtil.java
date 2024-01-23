package birdeye.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.function.Function;

public class WaitUtil {
    public static WebElement fluentWaitForWebElt(WebDriver driver, final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(50))
                .pollingEvery(Duration.ofSeconds(5)).ignoring(NoSuchElementException.class);

        WebElement webElement = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);

            }
        });
        return webElement;
    };

    public static WebElement fluentWaitForWebElt(WebDriver driver, final By locator, int maxTimeInSec,
                                                 int pollingTime) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(maxTimeInSec))
                .pollingEvery(Duration.ofSeconds(pollingTime)).ignoring(NoSuchElementException.class);

        WebElement webElement = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });
        return webElement;
    };

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
