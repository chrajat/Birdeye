package home.tests;

import org.apache.hc.core5.reactor.Command;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import birdeye.base.TestBase;
import home.methods.HomePageMethods;
import home.pageobjects.HomePageObjects;
import home.methods.LoginPageMethods;
import birdeye.util.TestUtil;
import birdeye.util.WaitUtil;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class HomePageTest extends TestBase {

	private HomePageObjects homePage;
	private HomePageMethods homePageMethods;
	private TestUtil testUtil;

	private WaitUtil waitUtil;

	private LoginPageMethods loginPageMethods;


	//test cases should be separated -- independent with each other
	//before each test case -- launch the browser and login
	//@test -- execute test case
	//after each test case -- close the browser

	@BeforeClass
	public void beforeClass() throws IOException {
		initialization();
		testUtil = new TestUtil();
		homePageMethods = new HomePageMethods();
		homePage = new HomePageObjects();
		waitUtil = new WaitUtil();

	}
/*
	@Test(priority=1)
	public void verifyHomePageTitleTest(){
		String homePageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("    Home page Title is here     "     +driver.getTitle());
		Assert.assertEquals(homePageTitle,"Leading Review Management & Messaging Platform for Local Businesses | Birdeye","Home page title not matched");

	}
	@Test(priority=2)
	public void verifyListingsPageTitleTest() throws InterruptedException {
		String baseKey = "url";
		String baseword ="PageTitle";
		int numberOfURLs = 15; // You can adjust this based on the number of URLs you have

		for (int i = 1; i <= numberOfURLs; i++) {
			String currentURLKey = baseKey + i;
			String expectedPageTitleword = baseword + i;
			String currentURL = prop.getProperty(currentURLKey);
			String expectedPageTitle = prop.getProperty(expectedPageTitleword);

			driver.get(currentURL);


			//String expectedListingsPageTitle = prop.getProperty("expectedPageTitle");
			String listingsPageTitle = homePageMethods.verifyHomePageTitle();

			System.out.println("page page Title is here: " + driver.getTitle());
			Assert.assertEquals(listingsPageTitle, expectedPageTitle, "Listings page title not matched for URL: " );
		}
	}





	public void verifyAllPagesResponseCode() throws InterruptedException {
		//driver.get("urlHome");
		// Get the page source
		String pageSource = driver.getPageSource();

		// Extract URLs using a regular expression
		Set<String> uniqueUrls = homePageMethods.extractUrls(pageSource);

		// Print the extracted URLs
		for (String url : uniqueUrls) {
			System.out.println(url);

		}

	}
*/
@Test(priority = 1)
	public void verifyPagesResponseCode() throws InterruptedException {

		String pageSource = driver.getPageSource();
		HashMap<String, Integer> urlResponse = new HashMap<String, Integer>();


		Set<String> uniqueUrls = homePageMethods.extractUrls(pageSource);
		System.out.println("\n");
		System.out.println("Total URLS matched with the shared REGEX is: " +uniqueUrls.size());


		// Print the extracted URLs
		for (String url : uniqueUrls) {

			// Print the status code
			int statusCode = homePageMethods.getResponseCode(url);
			urlResponse.put(url, statusCode);
			System.out.println(urlResponse);
		}
	urlResponse.forEach((key, value) -> {
		System.out.println("------URL: ------> " + key + ", -------Status: " + value);
	});
	// Print URLs and their statuses only if status is not 200
	urlResponse.forEach((url, statusCode) -> {
		if (statusCode != 200) {
			System.out.println("URL with errors " + url + ", fault error: " + statusCode);
		}
		/*
		urlResponse.forEach((url, statusCode) -> {
    if (statusCode == 301 || statusCode == 404) {
        System.out.println("URL with errors " + url + ", fault errors: " + statusCode);
    }
});
*/
	});
}


	@Test(priority=2)
	public void verifyPagesResponseCMP() throws InterruptedException {

		String baseKey = "URL";
		int numberOfURLs = 57; // You can adjust this based on the number of URLs you have

		for (int i = 1; i <= numberOfURLs; i++) {
			String currentURLKey = baseKey + i;// Assuming you get the URL value from system properties

			String url = prop.getProperty(currentURLKey);


			// Print the status code
			int statusCode = homePageMethods.getResponseCode(url);
			//urlResponses.put(url, statusCode);

		//urlResponse.forEach((key, value) -> {
			//System.out.println("------URL: ------> " + url + ", -------Status: " + statusCode);

					if (statusCode != 200) {
						System.out.println("URL with errors " + url + ", fault error: " + statusCode);
					}

				}

	}




/*
	@Test(priority=2)
	public void verifylistingsPageTitleTest() throws InterruptedException {
		driver.get(prop.getProperty("urlListings"));
		String expectedlistingsPageTitle = prop.getProperty("listingsPageTitle");
		String listingsPageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("   Listings page Title is here     "     +driver.getTitle());
		Assert.assertEquals(listingsPageTitle, expectedlistingsPageTitle, "Listings page title not matched");
	}

	@Test(priority=3)
	public void verifyreviewsPageTitleTest() throws InterruptedException {
		driver.get(prop.getProperty("urlReviews"));
		String expectedReviewsPageTitle = prop.getProperty("reviewsPageTitle");
		String reviewsPageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("   Reviews page Title is here     "     +driver.getTitle());
		Assert.assertEquals(reviewsPageTitle, expectedReviewsPageTitle, "Reviews page title not matched");
	}



	@Test(priority=4)
	public void verifyreferralsPageTitleTest() throws InterruptedException {
		driver.get(prop.getProperty("urlReferrals"));
		String expectedReferralsPageTitle = prop.getProperty("referralsPageTitle");
		String referralsPageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("   Referrals page Title is here     "     +driver.getTitle());
		Assert.assertEquals(referralsPageTitle,expectedReferralsPageTitle,"Referrals page title not matched");
	}

	@Test(priority=5)
	public void verifyPagesPageTitleTest() throws InterruptedException {
		driver.get(prop.getProperty("urlPages"));
		String expectedpagesPageTitle = prop.getProperty("pagesPageTitle");
		String pagesPageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("   Pages page Title is here     "     +driver.getTitle());
		Assert.assertEquals(pagesPageTitle,expectedpagesPageTitle,"Pages page title not matched");
	}






	@Test(priority=2)
	public void verifyUserLoginFlow() throws InterruptedException {
		WaitUtil.sleep(1000);
		homePage.signInlink().click();
		boolean logoImage = loginPageMethods.getBirdeyeLogoOnLoginpage().isDisplayed();
		Assert.assertTrue(logoImage,"User not redirect to the Login Page");
		//Assert.assertTrue(homePage.verifyCorrectUserName());
	}

	@Test(priority=3)
	public void verifyContactsLinkTest(){
		testUtil.switchToFrame();
		contactsPage = homePage.clickOnContactsLink();
	}

	*/
	
	@AfterClass
	public void tearDown(){
		driver.quit();
	}

	

}
