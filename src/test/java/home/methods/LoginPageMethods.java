package home.methods;

import home.pageobjects.LoginPageObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPageMethods {
    private WebDriver driver;
    private LoginPageObjects loginPageObjects;

    public LoginPageMethods(WebDriver driver){
        this.driver = driver;
        loginPageObjects = PageFactory.initElements(driver,LoginPageObjects.class);
    }

    public WebElement getBirdeyeLogoOnLoginpage(){
        return loginPageObjects.getBirdeyeLogoOnLoginpage();
    }
}



