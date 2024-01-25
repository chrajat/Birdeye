package home.tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import birdeye.base.TestBase;
import home.methods.HomePageMethods;
import home.pageobjects.HomePageObjects;
import home.methods.LoginPageMethods;
import birdeye.util.TestUtil;
import birdeye.util.WaitUtil;


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


*/
	@Test(priority=2)

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
