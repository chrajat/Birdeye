package home.tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

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
	
	
	@Test(priority=1)
	public void verifyHomePageTitleTest(){
		String homePageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("    Home page Title is here     "     +driver.getTitle());
		Assert.assertEquals(homePageTitle,"Leading Review Management & Messaging Platform for Local Businesses | Birdeye","Home page title not matched");
	}

	@Test(priority=2)
	public void verifylistingsPageTitleTest() throws InterruptedException {
		driver.get("https://birdeye.com/listings/");
		String listingsPageTitle = homePageMethods.verifyHomePageTitle();
		System.out.println("   Listings page Title is here     "     +driver.getTitle());
		Assert.assertEquals(listingsPageTitle,"Business Listings Management Software | Birdeye","Listings page title not matched");
	}

	/*
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
