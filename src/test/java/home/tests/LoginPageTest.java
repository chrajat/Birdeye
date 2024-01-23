package home.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import birdeye.base.TestBase;
import home.pageobjects.HomePageObjects;
import home.pageobjects.LoginPageObjects;


public class LoginPageTest extends TestBase{
	LoginPageObjects loginPage;
	HomePageObjects homePage;
	
	public LoginPageTest(){
		super();
	}
	
	@BeforeMethod
	public void setUp(){
		initialization();
		loginPage = new LoginPageObjects();
	}
	
	/*@Test(priority=1)
	public void loginPageTitleTest(){
		String title = loginPage.validateLoginPageTitle();
		Assert.assertEquals(title, "#1 Free CRM for Any Business: Online Customer Relationship Software");
	}*/
	
	@Test(priority=2)
	public void birdeyeLogoImg(){
		boolean flag = loginPage.getBirdeyeLogoOnLoginpage().isDisplayed();
		Assert.assertTrue(flag);
	}
	
	//@Test(priority=3)
	/*public void loginTest(){
		homePage = loginPage.login(prop.getProperty("username"), prop.getProperty("password"));
	}*/
	
	
	
	@AfterMethod
	public void tearDown(){
		driver.quit();
	}
	
	
	
	

}
