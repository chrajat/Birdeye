package common.utilities.browser.localbrowser;

//import common.launchsetup.AppiumService;
import common.launchsetup.BaseTest;
import common.launchsetup.Config;
import common.utilities.browser.IBrowserUtil;
//import io.appium.java_client.android.AndroidDriver;
//import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.exec.ExecuteException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class LocalBrowserUtilWap implements IBrowserUtil {

	private final String pathAppender = "src/main/resources/drivers/";
	private RemoteWebDriver driver;

	private String browser;
	private DesiredCapabilities desiredCapabilities = null;
//	private AppiumService appiumService;

	/*
	 * Launches the browser, browser name need to provided via argument
	 */
	@Override
	public RemoteWebDriver launchBrowser() throws IOException {
		//String browser_driver_path = System.getProperty("user.dir") + pathAppender;
		String browser_driver_path =  pathAppender;
		browser = BaseTest.browserName == null
				? new Config().fetchConfig(new File("./suiterun.properties"), "browserName")
				: BaseTest.browserName;
		switch (browser) {

		case ("androidChrome"):
//			new AppiumService().startAppiumService();
			setDesiredCapabilitiesWAP("androidChrome");
			System.setProperty("webdriver.chrome.driver", browser_driver_path + "chromedriver.exe");
			// WebDriverManager.chromedriver().setup();
			// WebDriverManager.chromedriver().driverVersion("98.0.4758.102").setup();

//			driver = new AndroidDriver(new URL(Config.fetchConfigProperty("baseUrl")), desiredCapabilities);
			break;

		case ("macSafari"):
//			new AppiumService().startAppiumService();
			setDesiredCapabilitiesWAP("macSafari");
//			driver = new IOSDriver(new URL(Config.fetchConfigProperty("baseUrl")), desiredCapabilities);
			break;

		case ("pwa"):
			String pwaBrowserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? (Platform.getCurrent().toString().equalsIgnoreCase("LINUX")? "chromedriverLinux" 
					: "chromedriver.exe"): "chromedriver";

			System.setProperty("webdriver.chrome.driver", browser_driver_path + pwaBrowserDriver);
			// WebDriverManager.chromedriver().setup();
			// WebDriverManager.chromedriver().driverVersion("98.0.4758.102").setup();
			ChromeOptions pwaMobileOptions = new ChromeOptions();
			if (!Platform.getCurrent().toString().equalsIgnoreCase("LINUX")) {
				 String pwaBrowserBinary =
			 !(Platform.getCurrent().toString().equalsIgnoreCase("MAC"))
				? "C:/chrome-win64/chrome.exe"
				: "/Users/chrome-mac-x64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing";

				 pwaMobileOptions.setBinary(pwaBrowserBinary);
			}
			pwaMobileOptions.addArguments("--disable-gpu");
			pwaMobileOptions.addArguments("--no-sandbox");
			pwaMobileOptions.addArguments("--disable-dev-shm-usage");
			pwaMobileOptions.addArguments("--headless");
			Map<String, String> pwaMobileEmulation = new HashMap<String, String>();
			pwaMobileEmulation.put("deviceName", "Nexus 5");
			Map<String, Object> pwaChromeOptions = new HashMap<String, Object>();
			pwaChromeOptions.put("mobileEmulation", pwaMobileEmulation);
			pwaMobileOptions.setExperimentalOption("mobileEmulation", pwaMobileEmulation);
			driver = new ChromeDriver(pwaMobileOptions);
			break;
		case ("specificDevice"):
			String deviceBrowserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? (Platform.getCurrent().toString().equalsIgnoreCase("LINUX")? "chromedriverLinux" 
					: "chromedriver.exe"): "chromedriver";
		
		
			System.setProperty("webdriver.chrome.driver", browser_driver_path + deviceBrowserDriver);
			// WebDriverManager.chromedriver().setup();
			ChromeOptions deviceMobileOptions = new ChromeOptions();
			
			if (!Platform.getCurrent().toString().equalsIgnoreCase("LINUX")) {
				 String sdBrowserBinary =
			 !(Platform.getCurrent().toString().equalsIgnoreCase("MAC"))
				? "C:/chrome-win64/chrome.exe"
				: "/Users/chrome-mac-x64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing";

				 deviceMobileOptions.setBinary(sdBrowserBinary);
			}
			deviceMobileOptions.addArguments("--no-sandbox");
			deviceMobileOptions.addArguments("--disable-dev-shm-usage");
			deviceMobileOptions.addArguments("--headless");
			Map<String, String> deviceMobileEmulation = new HashMap<String, String>();
			deviceMobileEmulation.put("deviceName", BaseTest.deviceName);
			Map<String, Object> deviceChromeOptions = new HashMap<String, Object>();
			deviceChromeOptions.put("mobileEmulation", deviceMobileEmulation);
			deviceMobileOptions.setExperimentalOption("mobileEmulation", deviceMobileEmulation);
			driver = new ChromeDriver(deviceMobileOptions);
			break;

		case ("specificUserAgent"):
			String userAgentBrowserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? (Platform.getCurrent().toString().equalsIgnoreCase("LINUX")? "chromedriverLinux" 
					: "chromedriver.exe"): "chromedriver";

			System.setProperty("webdriver.chrome.driver", browser_driver_path + userAgentBrowserDriver);
			// WebDriverManager.chromedriver().setup();
			ChromeOptions userAgentMobileOptions = new ChromeOptions();
			if (!Platform.getCurrent().toString().equalsIgnoreCase("LINUX")) {
				 String uaBrowserBinary =
			 !(Platform.getCurrent().toString().equalsIgnoreCase("MAC"))
				? "C:/chrome-win64/chrome.exe"
				: "/Users/chrome-mac-x64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing";

				 userAgentMobileOptions.setBinary(uaBrowserBinary);
			}
			userAgentMobileOptions.addArguments("--no-sandbox");
			userAgentMobileOptions.addArguments("--disable-dev-shm-usage");
			userAgentMobileOptions.addArguments("--headless");
			Map<String, Object> userAgentMobileEmulation = new HashMap<String, Object>();
			Map<String, Object> deviceMetrics = new HashMap<>();
			deviceMetrics.put("width", 400);
			deviceMetrics.put("height", 450);
			deviceMetrics.put("pixelRatio", 3.0);
			userAgentMobileEmulation.put("deviceMetrics", deviceMetrics);
			userAgentMobileEmulation.put("userAgent", BaseTest.userAgent);
			Map<String, Object> userAgentChromeOptions = new HashMap<String, Object>();
			userAgentChromeOptions.put("mobileEmulation", userAgentMobileEmulation);
			userAgentMobileOptions.setExperimentalOption("mobileEmulation", userAgentMobileEmulation);
			driver = new ChromeDriver(userAgentMobileOptions);
			break;

		default:

			String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? (Platform.getCurrent().toString().equalsIgnoreCase("LINUX")? "chromedriverLinux" 
					: "chromedriver.exe"): "chromedriver";
			System.setProperty("webdriver.chrome.driver", browser_driver_path + browserDriver);
			// WebDriverManager.chromedriver().setup();
			// WebDriverManager.chromedriver().driverVersion("98.0.4758.102").setup();
			ChromeOptions mobileOptions = new ChromeOptions();
			
			if (!Platform.getCurrent().toString().equalsIgnoreCase("LINUX")) {
				 String browserBinaryDefault =
			 !(Platform.getCurrent().toString().equalsIgnoreCase("MAC"))
				? "C:/chrome-win64/chrome.exe"
				: "/Users/chrome-mac-x64/Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing";

				 mobileOptions.setBinary(browserBinaryDefault);
			}
			
			mobileOptions.addArguments("--no-sandbox");
			mobileOptions.addArguments("--disable-dev-shm-usage");
			mobileOptions.addArguments("--headless");
			mobileOptions.addArguments("--disable-gpu");
			Map<String, String> mobileEmulation = new HashMap<String, String>();
			String deviceName = getRandomDeviceName();
			mobileEmulation.put("deviceName", deviceName);
			Map<String, Object> chromeOptions = new HashMap<String, Object>();
			chromeOptions.put("mobileEmulation", mobileEmulation);
			mobileOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
			driver = new ChromeDriver(mobileOptions);
		}
		if (Config.fetchConfigProperty("ElementWaitTime") != null)
			driver.manage().timeouts().implicitlyWait(Long.parseLong(Config.fetchConfigProperty("ElementWaitTime")),
					TimeUnit.SECONDS);
		return driver;
	}

	private void setDesiredCapabilitiesWAP(String platformBrowser) {
		desiredCapabilities = new DesiredCapabilities();
		Map<String, String> keyValue = new LinkedHashMap<>();
		if (platformBrowser.equalsIgnoreCase("androidChrome")) {
			keyValue = Config.fetchMatchingProperty("aChrome");
		} else if (platformBrowser.equalsIgnoreCase("macSafari")) {
			desiredCapabilities.setCapability("nativeInstrumentsLib", true);
			desiredCapabilities.setCapability("nativeWebTap", true);
			keyValue = Config.fetchMatchingProperty("mSafari");
		}
		Iterator<Entry<String, String>> it = keyValue.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> pair = (Entry<String, String>) it.next();
			desiredCapabilities.setCapability((String) pair.getKey(), pair.getValue());
		}
	}

	public void killAppium() throws ExecuteException, IOException {
//		appiumService.stopAppiumService();
	}

	@Override
	public void killServices() {
		if (driver != null)
			driver.quit();
//		if (BaseTest.platform.contains("App"))
//			appiumService.stopAppiumService();

	}

	public String getRandomDeviceName() {
		String serviceId = "";
		int randomNumber = 0;
		try {
			Random rand01 = new Random();
			randomNumber = rand01.nextInt(5);
			String[] serviceIdArr = "Moto G4, Galaxy S5, Pixel 2, Pixel 2 XL, Galaxy Note 3".split(", ");
			serviceId = serviceIdArr[randomNumber];
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		System.out.println("The random number is:" + randomNumber + " the random device is " + serviceId);
		return serviceId;
	}
}
