package home.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPageObjects {

    //Page Factory - OR:
    @FindBy(name="username")
    WebElement username;

    @FindBy(name="password")
    WebElement password;

    @FindBy(xpath="//input[@type='submit']")
    WebElement loginBtn;

    @FindBy(xpath="//button[contains(text(),'Sign Up')]")
    WebElement signUpBtn;


    @FindBy(id ="brand-href")
    WebElement birdeyeLogo;



    //@FindBy(xpath="//img[contains(@class,'img-responsive')]")
    //WebElement crmLogo;

    //Initializing the Page Objects:
	/*public LoginPage(){
		PageFactory.initElements(driver, this);
	}*/

    //Actions:


    public WebElement getBirdeyeLogoOnLoginpage(){
        return birdeyeLogo;
    }

	/*
	public HomePage login(String un, String pwd){
		username.sendKeys(un);
		password.sendKeys(pwd);
		//loginBtn.click();
		    	JavascriptExecutor js = (JavascriptExecutor)driver;
		    	js.executeScript("arguments[0].click();", loginBtn);

		return new HomePage();
	}
	 */

}
