package common.utilities.browser;

import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;

public interface IBrowserUtil {

	public RemoteWebDriver launchBrowser()  throws MalformedURLException, IOException;

	public void killServices();
}
