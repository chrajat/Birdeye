package home.pageobjects;

import birdeye.base.BaseTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.PageFactory;


public class HomePageObjects extends BaseTest {

	@FindBy(xpath = "//td[contains(text(),'User: Naveen K')]")
	@CacheLookup
	WebElement userNameLabel;

	@FindBy(xpath = "//a[contains(text(),'Contacts')]")
	WebElement contactsLink;
	
	@FindBy(xpath = "//a[contains(text(),'New Contact')]")
	WebElement newContactLink;
	

	@FindBy(xpath = "//a[contains(text(),'Deals')]")
	WebElement dealsLink;

	@FindBy(xpath = "//a[contains(text(),'Tasks')]")
	WebElement tasksLink;

	@FindBy(xpath = "//a[contains(text(),'Sign In')]")
	WebElement signInLink;

	@FindBy(id = "signInlink")
	WebElement signInLinkOnLoginPage;

	// Initializing the Page Objects:
	public HomePageObjects() {
		PageFactory.initElements(driver, this);
	}
	
	public String verifyHomePageTitle(){
		return driver.getTitle();
	}

	public WebElement signInlink(){
		return signInLink;
	}

	public String signInLinkOnLoginPage(){
		return signInLinkOnLoginPage.getText();
	}
	
	
	public boolean verifyCorrectUserName(){
		return userNameLabel.isDisplayed();
	}
	

	
	public void clickOnNewContactLink(){
		Actions action = new Actions(driver);
		action.moveToElement(contactsLink).build().perform();
		newContactLink.click();
		
	}


}
