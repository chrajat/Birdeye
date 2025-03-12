package common.utilities.browser;

import common.launchsetup.BaseTest;
import common.launchsetup.Config;
import common.utilities.WaitUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.mitm.TrustSource;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import web.pagemethods.WebBaseMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProxyBrowserUtil implements IBrowserUtil {
	private final String pathAppender = "/src/main/resources/";
	private final String resourcePath = "." + pathAppender;
	private static WebDriver driver;
	private String harFile = "./XHRCalls/proxy.har";
	private static BrowserMobProxy proxy;
	private static ProxyBrowserUtil proxyBrowserUtil;

	public static ProxyBrowserUtil getInstance() {
		proxyBrowserUtil = (ProxyBrowserUtil) BaseTest.browserUtil;
		return proxyBrowserUtil;
	}

	/**
	 * Platform here is a dummy value matching the interface rules
	 */
	@Override
	public RemoteWebDriver launchBrowser() throws MalformedURLException, IOException {
		String browser_driver_path = System.getProperty("user.dir") + pathAppender + "drivers/";
		proxy = new BrowserMobProxyServer();
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS);
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT);
		proxy.enableHarCaptureTypes(CaptureType.RESPONSE_HEADERS);
		proxy.enableHarCaptureTypes(CaptureType.RESPONSE_CONTENT);
		proxy.setTrustAllServers(true);
		proxy.setTrustSource(TrustSource.defaultTrustSource());
		proxy.start(0);
		if (BaseTest.platform.equalsIgnoreCase("WAP")) {
			// get the selenium proxy object
			Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
			try {
				String hostIp = Inet4Address.getLocalHost().getHostAddress();
				seleniumProxy.setHttpProxy(hostIp + ":" + proxy.getPort());
				seleniumProxy.setSslProxy(hostIp + ":" + proxy.getPort());
			} catch (UnknownHostException e) {

			}
			ChromeOptions options = new ChromeOptions();
			options.setBinary("C:\\chrome-win64\\chrome.exe");
			options.addArguments("--ignore-certificate-errors");
			// options.addArguments("--user-data-dir=./XHRCalls");
			options.setCapability(CapabilityType.PROXY, seleniumProxy);
			//options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

			Map<String, String> mobileEmulation = new HashMap<String, String>();
			mobileEmulation.put("deviceName", "iPhone 6");
			Map<String, Object> chromeOptions = new HashMap<String, Object>();
			chromeOptions.put("mobileEmulation", mobileEmulation);
			options.setExperimentalOption("mobileEmulation", mobileEmulation);

			String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? "chromedriver.exe"
					: "chromedriver_mac";
			System.setProperty("webdriver.chrome.driver", browser_driver_path
			 + browserDriver);
			//WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
		} else if (BaseTest.platform.equalsIgnoreCase("PWA")) {
			Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

			ChromeOptions options = new ChromeOptions();
			options.setBinary("C:\\chrome-win64\\chrome.exe");
			options.addArguments("--ignore-certificate-errors");
			// options.addArguments("--user-data-dir=./XHRCalls");
			options.setCapability(CapabilityType.PROXY, seleniumProxy);
			//options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

			Map<String, String> mobileEmulation = new HashMap<String, String>();
			mobileEmulation.put("deviceName", "Nexus 5");
			Map<String, Object> chromeOptions = new HashMap<String, Object>();
			chromeOptions.put("mobileEmulation", mobileEmulation);
			options.setExperimentalOption("mobileEmulation", mobileEmulation);

			String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? "chromedriver.exe"
					: "chromedriver_mac";
			System.setProperty("webdriver.chrome.driver", browser_driver_path
			+ browserDriver);
			//WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
		} else if (BaseTest.platform.equalsIgnoreCase("Web")) {
			if (BaseTest.browserName.equals("Chrome")) {
				Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
				ChromeOptions options = new ChromeOptions();
				options.setBinary("C:\\chrome-win64\\chrome.exe");
				options.addArguments("--ignore-certificate-errors");
				// options.addArguments("--user-data-dir=./XHRCalls");
				options.setCapability(CapabilityType.PROXY, seleniumProxy);
				//options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				
				String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? "chromedriver.exe"
						: "chromedriver_mac";
				System.setProperty("webdriver.chrome.driver",
				 browser_driver_path + browserDriver);
				//WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver(options);
			} else if (BaseTest.browserName.equalsIgnoreCase("chromeExtended")) {
				Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
				ChromeOptions options = new ChromeOptions();
				options.setBinary("C:\\chrome-win64\\chrome.exe");
				options.addArguments("--ignore-certificate-errors");
				// options.addArguments("--user-data-dir=./XHRCalls");
				options.setCapability(CapabilityType.PROXY, seleniumProxy);
				//options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				File proxyPropFile = (new File(
						resourcePath + "properties/" + Config.fetchConfigProperty("proxyBrowserFilePath")));
				String extensions = new Config().fetchConfig(proxyPropFile, "extensionToLoad");
				List<String> listOfExtensions = Arrays.asList(extensions.split("\\s*,\\s*"));
				listOfExtensions.forEach(extension -> {
					extension = resourcePath + "extensions/" + extension;
					options.addExtensions(new File(extension));
				});
				String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? "chromedriver.exe"
						: "chromedriver_mac";
				System.setProperty("webdriver.chrome.driver",
				browser_driver_path + browserDriver);
				//WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver(options);
			} else {
				String browserDriver = !(Platform.getCurrent().toString().equalsIgnoreCase("MAC")) ? "geckodriver.exe"
						: "geckodriver_mac";
				// System.setProperty("webdriver.gecko.driver",
				// browser_driver_path + browserDriver);
				WebDriverManager.firefoxdriver().setup();
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("network.proxy.http", "localhost");
				profile.setPreference("network.proxy.http_port", proxy.getPort());
				profile.setPreference("network.proxy.ssl", "localhost");
				profile.setPreference("network.proxy.ssl_port", proxy.getPort());
				profile.setPreference("network.proxy.type", 1);
				FirefoxOptions firefoxoptions = new FirefoxOptions();
				//firefoxoptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				firefoxoptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				//firefoxoptions.setCapability(FirefoxDriver.PROFILE, profile);
				firefoxoptions.setCapability("marionette", true);

				try {
					driver = new FirefoxDriver(firefoxoptions);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		return (RemoteWebDriver) driver;

	}

	public File returnHARFile(String url) {
		proxy.newHar();
		try {
			driver.get(url);
			if (driver.getCurrentUrl().contains("interstitial")) {
				WaitUtil.sleep(10000);
				proxy.newHar();
				driver.get(url);
			}
		} catch (TimeoutException e) {
			return null;
		}
		WaitUtil.sleep(10000);
		net.lightbody.bmp.core.har.Har har = proxy.getHar();
		FileOutputStream fos;
		File harOutputFile = new File(harFile);
		try {
			if (harOutputFile.exists()) {
				harOutputFile.delete();
			}
			harOutputFile.createNewFile();
			fos = new FileOutputStream(harOutputFile);
			har.writeTo(fos);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proxy.endHar();
		return harOutputFile;
	}

	public File returnHARFileAfterScrolling(String url, int noOfScrolls, String direction, int pixels) {

		proxy.newHar();
		try {
			driver.get(url);
			if (driver.getCurrentUrl().contains("interstitial")) {
				WaitUtil.sleep(10000);
				System.out.println("hi I am here in If block");
				addCookieForInterstitialAd();
				proxy.newHar();
				driver.get(url);
			}
		} catch (TimeoutException e) {
			return null;
		}
		WaitUtil.sleep(10000);
		WebBaseMethods.scrollMultipleTimes(noOfScrolls, direction, pixels);
		net.lightbody.bmp.core.har.Har har = proxy.getHar();
		FileOutputStream fos;
		File harOutputFile = new File(harFile);
		try {
			if (harOutputFile.exists()) {
				harOutputFile.delete();
			}
			harOutputFile.createNewFile();
			fos = new FileOutputStream(harOutputFile);
			har.writeTo(fos);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proxy.endHar();
		return harOutputFile;

	}

	public File returnAfterScrollHARFile(int noOfScrolls, String direction) {
		proxy.newHar();

		WebBaseMethods.scrollFixedHeightMultipleTimes(noOfScrolls, direction);
		// if(!url.contains("m.economictimes"))
		WaitUtil.sleep(8000);

		net.lightbody.bmp.core.har.Har har = proxy.getHar();
		FileOutputStream fos;
		File harOutputFile = new File(harFile);
		try {
			if (harOutputFile.exists()) {
				harOutputFile.delete();

			}
			harOutputFile.createNewFile();
			fos = new FileOutputStream(harOutputFile);
			har.writeTo(fos);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		proxy.endHar();
		return harOutputFile;
	}

	@Override
	public void killServices() {
		try {
			proxy.stop();
			if (driver != null)
				driver.quit();
		} catch (Exception e) {
			// do nothing
		}
	}

	public void addCookieForInterstitialAd() {
		try {
			if (BaseTest.platform.equalsIgnoreCase("web")) {
				driver.manage().getCookieNamed("nsIstial_Cook");
				driver.manage().deleteCookieNamed("nsIstial_Cook");
				Cookie ck = new Cookie("nsIstial_Cook", "2");
				driver.manage().addCookie(ck);
				System.out.println(driver.manage().getCookieNamed("nsIstial_Cook"));
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

}
