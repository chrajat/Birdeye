package home.methods;
import home.pageobjects.HomePageObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.net.HttpURLConnection;
import java.net.URL;
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

    // Extract URLs using a regular expression
    // To get all URLs from pageSource
    public static Set<String> extractUrls(String pageSource) {
        Set<String> uniqueUrls = new HashSet<>();

        // Define a regular expression pattern for extracting URLs
        //String regexPattern = "https://birdeye\\.com/([a-zA-Z0-9-_/]+)";

        Pattern pattern = Pattern.compile("https://birdeye.com/enterprise([a-zA-Z0-9-_./]+)");

        // Create a Matcher object
        Matcher matcher = pattern.matcher(pageSource);

        // Find all matches
        while (matcher.find()) {
            System.out.println("---------Matched URLS------------->  "+matcher.group());
            // Add each matched URL to the set to ensure uniqueness
            uniqueUrls.add(matcher.group());
        }

        return uniqueUrls;
    }


// to get the response code of urls shared in String
    public static int getResponseCode(String urlString) {
        int statusCode = -1; // Default value for error

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            // Get the status code
            statusCode = httpURLConnection.getResponseCode();

            // Close the connection
            httpURLConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusCode;
    }


}

