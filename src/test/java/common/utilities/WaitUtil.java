package common.utilities;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.google.common.base.Function;
import common.launchsetup.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import web.pagemethods.WebBaseMethods;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

	public static WebDriverWait explicitWaitByPresenceOfElement(WebDriver driver, int seconds, By by) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
		return wait;
	}

	public static WebDriverWait explicitWaitByPresenceOfElementbyWebEl(WebDriver driver, int seconds, WebElement el) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.presenceOfElementLocated(WebBaseMethods.getLocatorByWebElement(el)));
		return wait;
	}

	public static WebDriverWait explicitWaitByPresenceOfElements(WebDriver driver, int seconds, By by) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		return wait;
	}

	public static WebDriverWait explicitWaitForTextToBePresentOnElt(WebDriver driver, WebElement webElement,
			String text, int seconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.textToBePresentInElement(webElement, text));
		return wait;
	}

	public static WebDriverWait explicitWaitByInVisibilityOfElement(WebDriver driver, int seconds, By locator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
		return wait;
	}

	/**
	 * 
	 * @param driver
	 * @param webElement
	 * @param timeoutSeconds
	 * @return
	 * 
	 */
	public static WebElement fluentWait(final WebDriver driver, final WebElement webElement, final int timeoutSeconds) {
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);

		return wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver webDriver) {
				return webElement;
			}
		});
	}

	public static void waitForLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(140));
		try {
			wait.until(pageLoadCondition);
		} catch (TimeoutException e) {
			driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
		}
	}

	public static void waitforUrlToChange(WebDriver driver, String url, int timeInsecs) {
		long waitTime = timeInsecs * 1000;
		boolean flag = false;
		while (waitTime > 0 && !flag) {
			if (driver.getCurrentUrl().equals(url)) {
				WaitUtil.sleep(1000);
				flag = false;
				waitTime = waitTime - 1000;
			} else
				flag = true;
		}
	}

	public static WebDriverWait explicitWaitByVisibilityOfElement(WebDriver driver, int seconds,
			WebElement webElement) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.visibilityOf(webElement));
		return wait;
	}

	public static WebDriverWait waitForURLToLoad(WebDriver driver, int seconds, String text) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.urlContains(text));
		return wait;
	}

	/**
	 * To find element untill it's displayed till the max time. If the element
	 * is not found within given time, it will return as {@null}
	 * 
	 * @param driver
	 * @param by
	 * @param maxWaitInSec
	 * @return
	 * @throws InterruptedException
	 */
	public static WebElement poolingWaitForEltWithoutException(WebDriver driver, By by, int maxWaitInSec)
			throws InterruptedException {
		WebElement elt = null;
		for (int i = 0; i < maxWaitInSec; i++) {
			try {
				elt = driver.findElement(by);
				if (elt.isDisplayed()) {
					break;
				}
			} catch (Exception e) {
				Thread.sleep(1000);
			}
		}
		return elt;
	}

	/**
	 * To find element until it's displayed till the max time. If the element is
	 * not found within given time, it will throw Exception
	 * 
	 * @param driver
	 * @param by
	 * @param maxWaitInSec
	 * @return
	 * @throws ElementNotFoundException
	 */
	public static WebElement poolingWaitForElt(WebDriver driver, WebElement element, int maxWaitInSec)
			throws Exception {
		WebElement elt = null;
		boolean eltFound = false;
		for (int i = 0; i < maxWaitInSec; i++) {
			try {
				if (element.isDisplayed()) {
					eltFound = true;
					break;
				}
			} catch (Exception e) {
				Thread.sleep(1000);
			}
		}

		if (eltFound == false)
			// throw new ElementNotFoundException;
			throw new ElementNotInteractableException("Element Not Found within " + maxWaitInSec + "secs");

		//

		return elt;
	}

	public static WebElement poolingWaitForElt(WebDriver driver, By locator, int maxWaitInSec) {
		WebElement elt = null;
		boolean eltFound = false;
		for (int i = 0; i < maxWaitInSec; i++) {
			try {
				elt = driver.findElement(locator);
				if (elt.isDisplayed()) {
					eltFound = true;
					break;
				}
			} catch (Exception e) {
				WaitUtil.sleep(1000);
			}
		}

		if (eltFound == false)
			// throw new ElementNotFoundException("WebElement :" + elt, "",
			// "Element Not Found within " + maxWaitInSec + "secs");
			throw new ElementNotInteractableException(
					"WebElement :" + elt + "Element Not Found within" + maxWaitInSec + "secs");
		return elt;
	}

	public static WebElement poolingWaitForPdp(WebDriver driver, By buyButtonLocator, int maxWaitInSec)
			throws Exception {
		WebElement elt = null;
		for (int i = 0; i < maxWaitInSec; i++) {
			try {
				elt = driver.findElement(buyButtonLocator);
				if (elt.isDisplayed()) {
					WaitUtil.sleep(3000);
					break;
				}
			} catch (Exception e) {
				Thread.sleep(1000);
			}
		}

		return elt;
	}

	public static void applyImplicitWait(WebDriver driver, int timeInSeconds) {
		driver.manage().timeouts().implicitlyWait(timeInSeconds, TimeUnit.SECONDS);
	}

	public static void explicitWaitByPresenceOfElement(WebDriver driver, int seconds, WebElement element) {
		By by = WebBaseMethods.getLocatorByWebElement(element);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	public static void explicitWaitByPresenceOfElements(WebDriver driver, int seconds, WebElement element) {
		By by = WebBaseMethods.getLocatorByWebElement(element);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
	}

	public static void explicitWaitByVisibilityOfAllElements(WebDriver driver, int seconds,
			List<WebElement> webElements) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.visibilityOfAllElements(webElements));
	}

	public static void explicitWaitByInVisibilityOfElement(WebDriver driver, int seconds, WebElement element) {
		By by = WebBaseMethods.getLocatorByWebElement(element);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}

	public static void explicitWaitByElementToBeClickable(WebDriver driver, int seconds, WebElement elt) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		wait.until(ExpectedConditions.elementToBeClickable(elt));
	}

	public boolean explicityWaitForElementDisappearance(WebDriver driver, By webelementLocator, Integer seconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
		Boolean element = wait.until(ExpectedConditions.invisibilityOfElementLocated(webelementLocator));
		return element;
	}

	public static void waitForAdToDisappear(WebDriver driver) {
		String currentURL = driver.getCurrentUrl();
		while (currentURL.contains("/ads/")) {
			sleep(1000);
			currentURL = driver.getCurrentUrl();
		}
	}

	public static void turnOffImplicitWaits(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	}

	public static void turnOnImplicitWaits(WebDriver driver) {
		if(Config.fetchConfigProperty("ElementWaitTime")!=null){
		driver.manage().timeouts().implicitlyWait(Long.parseLong(Config.fetchConfigProperty("ElementWaitTime")),
				TimeUnit.SECONDS);
		}
	}

}
