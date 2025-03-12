/**
 * 
 */

package common.launchsetup;

//import common.utilities.AppLoggingHelper;
import common.utilities.FileUtility;
import common.utilities.WaitUtil;
import common.utilities.browser.IBrowserUtil;
import common.utilities.browser.ProxyBrowserUtil;
import common.utilities.browser.RemoteBrowserUtil;
import common.utilities.browser.localbrowser.LocalBrowserUtilWap;
import common.utilities.browser.localbrowser.LocalBrowserUtilWeb;
import common.utilities.reporting.ScreenShots;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.*;
//import web.pagemethods.ETSharedMethods;

import java.io.File;
import java.io.IOException;

public class BaseTest {
  private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

  public static RemoteWebDriver driver;
  public static Config config;
  public static IBrowserUtil browserUtil;
  public static String platform;
  public static String browserName;
  public static String baseUrl;
  public static String primeUrl;
  public static String deviceName;
  public static String userAgent;

  public static boolean globalFlag = true;
  public static Boolean globalFlag2 = true;
 
  public static int excelTestDataSheet;
  public static String environmentGiven;

  @BeforeSuite
  public void doCleanUps() {
    ScreenShots.cleanAllScreenShots();
  }

  @Parameters({"platform", "natureOfBrowser", "recipient", "failureRecipient", "mailSubject",
      "mailBody", "emailType", "dbFlag", "browser", "baseUrl", "deviceEmulatorName",
      "deviceUserAgent", "environment"})
  @BeforeTest(alwaysRun = true)
  public void beforeClass(@Optional("") String extPlatform,
      @Optional("local") String natureOfBrowser, @Optional("") String recipient,
      @Optional("") String failureRecipient, @Optional("") String mailSubject,
      @Optional("PFB execution report") String mailBody, @Optional("steadFast") String emailType,
      @Optional("false") String dbFlag, @Optional("") String browser, @Optional("") String baseUrl,
      @Optional("Galaxy S5") String deviceEmulatorName, @Optional("") String deviceUserAgent,
      @Optional("live") String environment) throws IOException {
    System.out.println(browser);
    platform = extPlatform.trim().length() > 0 ? extPlatform
        : new Config().fetchConfig(new File("./suiterun.properties"), "platform");
    browserName = browser.trim().length() > 0 ? browser
        : new Config().fetchConfig(new File("./suiterun.properties"), "browserName");
    BaseTest.platform = Platform.valueOf(platform).toString();
    deviceName = deviceEmulatorName.trim().length() > 0 ? deviceEmulatorName : "Nexus 5";
    userAgent = deviceUserAgent.trim().length() > 0 ? deviceUserAgent : null;

    log.info("================  Test will be executed for platform : " + BaseTest.platform
        + ", Nature of Browser : " + natureOfBrowser + " and Environment : " + environment
        + " ==================");
    Reporter.log("Provided Platform at Run Time ==> " + BaseTest.platform);
    FileUtility.writeIntoPropertiesFile(recipient, failureRecipient, mailSubject, mailBody,
        emailType, dbFlag);
    if (natureOfBrowser.equalsIgnoreCase("local")) {
      if (platform.equalsIgnoreCase("Web"))
          browserUtil = new LocalBrowserUtilWeb();
      else if (platform.equalsIgnoreCase("WAP")) {
        browserUtil = new LocalBrowserUtilWap();
      }
    }

    else if (natureOfBrowser.equalsIgnoreCase("remote"))
      if (platform.equalsIgnoreCase("Web")) {
        browserUtil = new RemoteBrowserUtil();
      } 
      else if (natureOfBrowser.equalsIgnoreCase("proxy"))
        browserUtil = new ProxyBrowserUtil();

      else
        browserUtil = new LocalBrowserUtilWeb();

    config = Config.getInstance(BaseTest.platform);
    BaseTest.baseUrl = baseUrl != null && baseUrl.trim().length() > 0 ? baseUrl
        : Config.fetchConfigProperty("HomeUrl");
    environmentGiven = environment;
    if (environment.equalsIgnoreCase("live")) {
      excelTestDataSheet = 0;
      primeUrl = Config.fetchConfigProperty("PrimeHomeUrl");

    } else if (environment.equalsIgnoreCase("dev")) {
      excelTestDataSheet = 1;
      primeUrl = Config.fetchConfigProperty("PrimeHomeUrlDev");
    } else {
      excelTestDataSheet = 0;
      primeUrl = Config.fetchConfigProperty("PrimeHomeUrl");

    }

    // BaseTest.primeUrl = Config.fetchConfigProperty("PrimeHomeUrlDev");
    // System.out.println("Base PrimeUrl is => "+BaseTest.primeUrl);
  }

  public void launchBrowser() throws IOException {
    if (driver == null || driver.getSessionId() == null)
      driver = browserUtil.launchBrowser();
    doStaticClassInitilaizations();
  }

  public void launchBrowser(String url) throws IOException {
    try {
      if (driver == null || driver.getSessionId() == null) {
        try {
          driver = browserUtil.launchBrowser();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      WaitUtil.sleep(1000);
      driver.get(url);
      WaitUtil.sleep(1000);
      doStaticClassInitilaizations();

//      if (platform.equalsIgnoreCase("WAP") && ETSharedMethods.isGDPRShown()) {
//        driver.navigate().refresh();
//      }
      if (driver.getCurrentUrl().contains("interstitial")) {
        if (BaseTest.platform.equalsIgnoreCase("Web"))
//          ETSharedMethods.clickETLinkInterstitialPage();
        if (BaseTest.platform.equalsIgnoreCase("WAP"))
          WaitUtil.sleep(6000);
      }

      if (platform.equalsIgnoreCase("Web") && url.contains("economictimes.indiatimes.com")) {
//        ETSharedMethods.declineNotifications();

      }
    } catch (TimeoutException e) {
      driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void tearDownLaunchBrowser(String url) throws IOException {
    if (driver != null) {
      driver.quit();
      driver = null;
    }
    if (driver == null)
      driver = browserUtil.launchBrowser();

    driver.get(url);
  }

  public void navigateTo(String url) {
    if (!driver.getCurrentUrl().equalsIgnoreCase(url))
      driver.get(url);
    else
      driver.navigate().refresh();
  }

  private void doStaticClassInitilaizations() {
    if (platform.equalsIgnoreCase("Web") || platform.equalsIgnoreCase("Wap")) {
//      ETSharedMethods.init(driver);
    }
  }

  @AfterSuite(alwaysRun = true)
  public void tearDown() {
    browserUtil.killServices();
  }
}
