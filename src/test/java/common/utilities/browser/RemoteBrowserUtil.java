package common.utilities.browser;

import common.launchsetup.BaseTest;
import common.launchsetup.Config;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

public class RemoteBrowserUtil implements IBrowserUtil {
	private RemoteWebDriver driver;
	private String remoteIP;
	private String port;

	private RemoteWebDriver initiateRemoteBrowser() throws IOException {
		Config config = Config.getInstance("RemoteBrowser.properties");

		remoteIP = config.fetchConfigProperty("remoteIP");
		port = config.fetchConfigProperty("port");

		String browser=BaseTest.browserName;
		String url = "http://" + remoteIP + ":" + port + "/wd/hub";

		if (browser.equalsIgnoreCase("firefox")) {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			//capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			driver = new RemoteWebDriver(new URL(url), capabilities);
		} else if (browser.equalsIgnoreCase("chrome")) {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--test-type");
			options.addArguments("--disable-popup-blocking");
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new RemoteWebDriver(new URL(url), capabilities);
		} else if (browser.equalsIgnoreCase("ie")) {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new RemoteWebDriver(new URL(url), capabilities);
		} else if (browser.equalsIgnoreCase("safari")) {
			driver = new RemoteWebDriver(new URL(url), new DesiredCapabilities());
		}

		driver.manage().window().maximize();

		System.out.println("A remote " + browser + " is launched on server : " + remoteIP + " for accessing the browser you can use below url:\n"
				+ url);
		Reporter.log("A remote " + browser + " is launched on server : " + remoteIP + " for accessing the browser you can use below url:\n" + url);
		return driver;
	}

	@Override
	public RemoteWebDriver launchBrowser() throws IOException {
		try {
			driver = initiateRemoteBrowser();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return driver;
	}

	@Override
	public void killServices() {
		if(driver!=null)
			driver.quit();
		
	}

	
}
