package common.utilities;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class GmailAPIUtil {
  private static final String APPLICATION_NAME = "ET App Automation";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
  private static final String CREDENTIALS_FILE_PATH = "credentials.json";

  private final Gmail service;

  public GmailAPIUtil() throws GeneralSecurityException, IOException {
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    service = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));

    // Build flow and trigger user authorization request
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public boolean checkNewsletterEmail(String senderEmail, String subject, int timeoutMinutes) {
    log.info("Checking for newsletter email from: {} with subject: {}", senderEmail, subject);
    try {
      LocalDateTime startTime = LocalDateTime.now();
      LocalDateTime endTime = startTime.plusMinutes(timeoutMinutes);

      while (LocalDateTime.now().isBefore(endTime)) {
        String query = String.format("from:%s subject:%s after:%s",
            senderEmail,
            subject,
            startTime.atZone(ZoneId.systemDefault()).toEpochSecond());

        ListMessagesResponse response = service.users().messages().list("me")
            .setQ(query)
            .execute();

        List<Message> messages = response.getMessages();
        if (messages != null && !messages.isEmpty()) {
          Message message = service.users().messages().get("me", messages.get(0).getId())
              .setFormat("full")
              .execute();

          // Get email received time
          long receivedTime = message.getInternalDate();
          LocalDateTime emailTime = LocalDateTime.ofInstant(
              new Date(receivedTime).toInstant(),
              ZoneId.systemDefault());

          if (emailTime.isAfter(startTime)) {
            log.info("Newsletter email found! Received at: {}", emailTime);
            return true;
          }
        }

        // Wait for 10 seconds before next check
        Thread.sleep(10000);
        log.info("Waiting for newsletter email... Time remaining: {} minutes",
            java.time.Duration.between(LocalDateTime.now(), endTime).toMinutes());
      }

      log.warn("Newsletter email not found within timeout period of {} minutes", timeoutMinutes);
      return false;

    } catch (Exception e) {
      log.error("Error checking newsletter email: {}", e.getMessage(), e);
      return false;
    }
  }

  public Map<String, String> getNewsletterEmailContent(String senderEmail, String subject) {
    Map<String, String> emailContent = new HashMap<>();
    try {
      String query = String.format("from:%s subject:%s", senderEmail, subject);

      ListMessagesResponse response = service.users().messages().list("me")
          .setQ(query)
          .execute();

      if (response.getMessages() != null && !response.getMessages().isEmpty()) {
        Message message = service.users().messages().get("me", response.getMessages().get(0).getId())
            .setFormat("full")
            .execute();

        // Extract email details
        emailContent.put("subject", message.getPayload().getHeaders().stream()
            .filter(header -> header.getName().equals("Subject"))
            .findFirst()
            .map(header -> header.getValue())
            .orElse(""));

        emailContent.put("from", message.getPayload().getHeaders().stream()
            .filter(header -> header.getName().equals("From"))
            .findFirst()
            .map(header -> header.getValue())
            .orElse(""));

        // Extract email body
        String body = extractEmailBody(message.getPayload());
        emailContent.put("body", body);

        log.info("Successfully retrieved newsletter email content");
      } else {
        log.warn("No matching email found");
      }

    } catch (Exception e) {
      log.error("Error getting newsletter email content: {}", e.getMessage(), e);
    }
    return emailContent;
  }

  private String extractEmailBody(com.google.api.services.gmail.model.MessagePart payload) {
    if (payload.getParts() == null) {
      // Handle base64 encoded body
      if (payload.getBody().getData() != null) {
        return new String(Base64.getUrlDecoder().decode(payload.getBody().getData()));
      }
      return "";
    }

    StringBuilder body = new StringBuilder();
    for (com.google.api.services.gmail.model.MessagePart part : payload.getParts()) {
      if (part.getMimeType().equals("text/plain")) {
        body.append(new String(Base64.getUrlDecoder().decode(part.getBody().getData())));
      }
    }
    return body.toString();
  }
}



