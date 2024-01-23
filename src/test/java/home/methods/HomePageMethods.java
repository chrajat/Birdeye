package home.methods;
import home.pageobjects.HomePageObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


public class HomePageMethods {

    private WebDriver driver;
    private HomePageObjects homePageObjects;

    public HomePageMethods() {
        this.driver = driver;
        homePageObjects = PageFactory.initElements(driver, HomePageObjects.class);
    }

    public String verifyHomePageTitle() {
        String homePageTitle = homePageObjects.verifyHomePageTitle();
        return homePageTitle;
    }


}

