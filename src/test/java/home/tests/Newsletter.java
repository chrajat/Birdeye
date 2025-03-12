package home.tests;

import common.utilities.GmailAPIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class Newsletter {
  private static Logger logger = LoggerFactory.getLogger(Newsletter.class);

  @Test(description = "Verify newsletter email received")
  public void verifyNewsletterEmailReceived() {
    logger.info("Starting newsletter email verification test");
    SoftAssert softAssert = new SoftAssert();


    try {
      GmailAPIUtil gmailAPI = new GmailAPIUtil();
      String senderEmail = "newsletter@economictimes.com";
      String expectedSubject = "Your ET Newsletter";
      int timeoutMinutes = 15;

      // Check if email is received
      boolean isEmailReceived = gmailAPI.checkNewsletterEmail(senderEmail, expectedSubject, timeoutMinutes);
      softAssert.assertTrue(isEmailReceived, "Newsletter email should be received within timeout period");

      if (isEmailReceived) {
        // Get email content for further verification
        Map<String, String>
            emailContent = gmailAPI.getNewsletterEmailContent(senderEmail, expectedSubject);

        // Verify email content
        softAssert.assertFalse(emailContent.isEmpty(), "Email content should not be empty");
        softAssert.assertEquals(emailContent.get("subject"), expectedSubject, "Email subject should match");
        softAssert.assertTrue(emailContent.get("from").contains(senderEmail), "Sender email should match");
        softAssert.assertFalse(emailContent.get("body").isEmpty(), "Email body should not be empty");

        // Log email details
        logger.info("Newsletter Email Details:");
        logger.info("From: {}", emailContent.get("from"));
        logger.info("Subject: {}", emailContent.get("subject"));
        logger.info("Body: {}", emailContent.get("body"));
      }

    } catch (Exception e) {
      logger.error("Error in newsletter email verification: {}", e.getMessage(), e);
      softAssert.fail("Test failed with exception: " + e.getMessage());
    }

    softAssert.assertAll();
  }
}

/*
Setup Instructions:
Create a Google Cloud Project and enable Gmail API:
1. Go to Google Cloud Console (https://console.cloud.google.com)
2. Create a new project
3. Enable Gmail API for your project
4. Create OAuth 2.0 credentials
5. Download the credentials and save as 'credentials.json' in your project root

Add the following to your .gitignore:
tokens/
credentials.json


Key Features:
Gmail API Integration:
Uses OAuth 2.0 for authentication
Supports reading emails with specific criteria
Handles base64 encoded email content
Email Verification:
Checks for emails from specific sender
Matches subject line
Verifies email received within timeout period
Extracts and validates email content
Robust Error Handling:
Detailed logging at each step
Proper exception handling
Clear error messages
Configurable Parameters:
Customizable timeout period
Flexible sender and subject matching


Reusable for different newsletter types
Usage Example:
// Initialize Gmail API utility
GmailAPIUtil gmailAPI = new GmailAPIUtil();

// Check for newsletter email
boolean isReceived = gmailAPI.checkNewsletterEmail(
    "newsletter@economictimes.com",
    "Your ET Newsletter",
    15  // timeout in minutes
);

// Get email content if needed
if (isReceived) {
    Map<String, String> content = gmailAPI.getNewsletterEmailContent(
        "newsletter@economictimes.com",
        "Your ET Newsletter"
    );
    // Process email content
}
Let me know if you need any clarification or have questions about the implementation!



 */
