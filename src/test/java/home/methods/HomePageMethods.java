package home.methods;
import home.pageobjects.HomePageObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public static Set<String> extractUrls(String pageSource) {
        Set<String> uniqueUrls = new HashSet<>();

        // Define a regular expression pattern for extracting URLs
        //String regexPattern = "https://birdeye\\.com/([a-zA-Z0-9-_/]+)";

        Pattern pattern = Pattern.compile("https://birdeye\\.com/([a-zA-Z0-9-_/]+)");

        // Create a Matcher object
        Matcher matcher = pattern.matcher(pageSource);

        // Find all matches
        while (matcher.find()) {
            // Add each matched URL to the set to ensure uniqueness
            uniqueUrls.add(matcher.group());
        }

        return uniqueUrls;
    }


}

