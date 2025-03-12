package common.utilities.browser.localbrowser;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import common.launchsetup.BaseTest;
import common.launchsetup.Config;
import common.utilities.browser.IBrowserUtil;
import io.github.bonigarcia.wdm.WebDriverManager;



public class LocalBrowserUtilWeb implements IBrowserUtil {

	private final String pathAppender = "src/main/resources/drivers/";
	private RemoteWebDriver driver;
	private String browser;

	/*
	 * Launches the browser, browser name need to provided via argument
	 */
	@Override
	public RemoteWebDriver launchBrowser() throws IOException {

		System.out.println("The platform on which testcase is running is: " + Platform.getCurrent().toString());
		// String browser_driver_path = System.getProperty("user.dir") + pathAppender;
		String /**/browser_driver_path = pathAppender;
		browser = BaseTest.browserName == null
				? new Config().fetchConfig(new File("./suiterun.properties"), "browserName")
				: BaseTest.browserName;
		if (browser != null && browser.equalsIgnoreCase("chrome")) {
			ChromeOptions options = new ChromeOptions();
			options.setPageLoadStrategy(PageLoadStrategy.NONE);

			options.addArguments("--test-type");
			options.addArguments("--disable-popup-blocking");
			options.addArguments("disable-infobars");
			options.addArguments("--disable-gpu");
			options.addArguments("--disable-features=VizDisplayCompositor");
			options.addArguments("--dns-prefetch-disable");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
//		 options.addArguments("--headless");
			String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC"))
					? (Platform.getCurrent().toString().equalsIgnoreCase("LINUX") ? "chromedriverLinux"
							: "chromedriver.exe")
					: "chromedriver";

			if (!(Platform.getCurrent().toString().equalsIgnoreCase("LINUX") || (Platform.getCurrent().toString().equalsIgnoreCase("MAC")))) {
				String browserBinaryDefault = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC"))
						? "C:/chrome-win64/chrome.exe"
						: "/Users/chrome-mac-x64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing";

				options.setBinary(browserBinaryDefault);
			}

			/// code to check setup on jenkins on ubuntu//////

			String browserDriverPath = browser_driver_path + browserDriver;
			System.out.println("The browser driver path is: " + browserDriverPath);
			System.setProperty("webdriver.chrome.driver", browserDriverPath);
			// WebDriverManager.chromedriver().setup();
			// WebDriverManager.chromedriver().driverVersion("95.0.4638.69").setup();

			driver = new ChromeDriver(options);
		} else if (browser != null && browser.equalsIgnoreCase("ie")) {
			// System.setProperty("cap_webdriver.ie.driver", browser_driver_path +
			// "IEDriverServer.exe");
			WebDriverManager.iedriver().setup();
			driver = new InternetExplorerDriver();
		} else {
			String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? "geckodriver.exe"
					: "geckodriver_mac";
			FirefoxOptions firefoxoptions = new FirefoxOptions();
			// System.setProperty("webdriver.gecko.driver", browser_driver_path +
			// browserDriver);

			WebDriverManager.firefoxdriver().setup();
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

			// firefoxoptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			firefoxoptions.setCapability("webdriver.log.driver", "OFF");

			driver = new FirefoxDriver(firefoxoptions);
		}
		driver.manage().window().maximize();
		if (Config.fetchConfigProperty("PageLoadTime") != null)
			driver.manage().timeouts()
					.pageLoadTimeout(Duration.ofSeconds(Long.parseLong(Config.fetchConfigProperty("PageLoadTime"))));
		if (Config.fetchConfigProperty("ElementWaitTime") != null)
			driver.manage().timeouts()
					.implicitlyWait(Duration.ofSeconds(Long.parseLong(Config.fetchConfigProperty("ElementWaitTime"))));
		return driver;

	}

	@Override
	public void killServices() {
		try {
			if (driver != null)
				driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
