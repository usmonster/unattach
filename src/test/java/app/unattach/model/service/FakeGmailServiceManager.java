package app.unattach.model.service;

import com.google.api.services.gmail.model.Message;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class FakeGmailServiceManager implements GmailServiceManager {
  private final String emailAddress;
  private final SortedMap<String, String> idToLabel;
  private final List<Message> messages;
  private final Map<String, String> beforeIdToAfterId;

  public FakeGmailServiceManager(String emailAddress, SortedMap<String, String> idToLabel, List<Message> messages,
                                 Map<String, String> beforeIdToAfterId) {
    this.emailAddress = emailAddress;
    this.idToLabel = idToLabel;
    this.messages = messages;
    this.beforeIdToAfterId = beforeIdToAfterId;
  }

  @Override
  public GmailService signIn() {
    return new FakeGmailService(emailAddress, idToLabel, messages, beforeIdToAfterId);
  }

  @Override
  public void signOut() {

  }
}
