package app.unattach.model;

import app.unattach.controller.LongTask;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class MockModel implements Model {
  private static final Logger LOGGER = Logger.getLogger(MockModel.class.getName());

  private final Config config = new BaseConfig();
  private final Random random = new Random(1337);
  private ArrayList<Email> emails = new ArrayList<>();

  @Override
  public void clearPreviousSearch() {
    emails = new ArrayList<>();
  }

  @Override
  public String createLabel(String name) {
    return "removed-label-id";
  }

  @Override
  public Config getConfig() {
    return config;
  }

  @Override
  public GetEmailMetadataTask getSearchTask(String query) {
    int minEmailSizeInMb = 1;
    List<String> emailIds = new ArrayList<>();
    emails = new ArrayList<>();
    int minEmailSizeInBytes = minEmailSizeInMb * (int) Math.pow(2, 20);
    int maxEmailId = 15;
    for (int i = minEmailSizeInBytes / 1000 / 1000; i < maxEmailId; ++i) {
      String emailId = String.valueOf(i);
      emailIds.add(emailId);
    }
    return new GetEmailMetadataTask(emailIds, (startIndexInclusive, endIndexExclusive) -> {
      for (int i = minEmailSizeInBytes / 1000 / 1000; i < maxEmailId; ++i) {
        if (startIndexInclusive <= i && i < endIndexExclusive) {
          String emailId = String.valueOf(i);
          emails.add(new Email(emailId, emailId, Arrays.asList("INBOX", "IMPORTANT"),
              "john.doe@example.com", "jane.doe@example.com",
              "Subject " + i, System.currentTimeMillis(),
              i * (int) Math.pow(2, 20)));
        }
      }
    });
  }

  @Override
  public String getEmailAddress() {
    return "user@mock.com";
  }

  @Override
  public List<Email> getEmails() {
    return emails;
  }

  @Override
  public SortedMap<String, String> getIdToLabel() {
    SortedMap<String, String> idToLabel = new TreeMap<>();
    for (int i = 0; i < 10; ++i) {
      idToLabel.put(String.valueOf(i), "Label Name " + i);
    }
    return idToLabel;
  }

  @Override
  public LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings) {
    return new ProcessEmailTask(email, e -> {
      if (random.nextBoolean()) {
        return new ProcessEmailResult(Collections.singleton(e.getGmailId()));
      } else {
        throw new IOException("Something went wrong.");
      }
    });
  }

  @Override
  public DefaultArtifactVersion getLatestVersion() {
    return new DefaultArtifactVersion(Constants.VERSION);
  }

  @Override
  public void signIn() {
    LOGGER.info("signIn");
  }

  @Override
  public void signOut() {
    LOGGER.info("signOut");
  }

  @Override
  public void sendToServer(String contentDescription, String userEmail, String stackTraceText, String userText) {
    LOGGER.info("========== sendToServer ==========");
    LOGGER.info(userEmail);
    LOGGER.info(stackTraceText);
    LOGGER.info(userText);
    LOGGER.info("========== sendToServer ==========");
  }

  @Override
  public void subscribe(String emailAddress) {
    LOGGER.info("subscribe: " + emailAddress);
  }
}
