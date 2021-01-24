package app.unattach.model;

import app.unattach.controller.LongTask;
import app.unattach.model.attachmentstorage.UserStorage;
import app.unattach.model.service.GmailService;
import app.unattach.model.service.GmailServiceException;
import app.unattach.model.service.GmailServiceManager;
import app.unattach.model.service.GmailServiceManagerException;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

public class LiveModel implements Model {
  private static final Logger LOGGER = Logger.getLogger(LiveModel.class.getName());

  private final Config config;
  private final UserStorage userStorage;
  private final GmailServiceManager gmailServiceManager;
  private GmailService service;
  private Session session;
  private List<Email> searchResults;
  private String emailAddress;

  public LiveModel(UserStorage userStorage, GmailServiceManager gmailServiceManager) {
    this.config = new FileConfig();
    this.userStorage = userStorage;
    this.gmailServiceManager = gmailServiceManager;
    configureMimeLibrary();
    reset();
  }

  private void configureMimeLibrary() {
    // see http://docs.oracle.com/javaee/6/api/javax/mail/internet/package-summary.html
    allowEmptyPartsInEmails();
    allowNonConformingEmailHeaders();
    // see https://stackoverflow.com/a/5292975/974531
    disablePrivateFetch();
    // see https://community.oracle.com/thread/1590013?start=0&tstart=0
    enableIgnoreErrors();
  }

  private void allowEmptyPartsInEmails() {
    System.setProperty("mail.mime.multipart.allowempty", "true");
  }

  private void allowNonConformingEmailHeaders() {
    System.setProperty("mail.mime.parameters.strict", "false");
  }

  private void disablePrivateFetch() {
    System.setProperty("mail.imaps.partialfetch", "false");
  }

  private void enableIgnoreErrors() {
    System.setProperty("mail.mime.base64.ignoreerrors", "true");
  }

  private void reset() {
    service = null;
    emailAddress = null;
    clearPreviousSearchResults();
  }

  @Override
  public void clearPreviousSearchResults() {
    searchResults = new ArrayList<>();
  }

  @Override
  public DefaultArtifactVersion getLatestVersion() throws IOException, InterruptedException {
    return HttpClient.getLatestVersion();
  }

  @Override
  public void signIn() throws GmailServiceManagerException {
    configureService();
    try {
      // Test call to the service. This can fail due to token issues.
      getEmailAddress();
    } catch (GmailServiceException e) {
      LOGGER.log(Level.WARNING, "Initial signing in failed. Explicitly signing out and retrying..", e);
      signOut();
      configureService();
    }
  }

  private void configureService() throws GmailServiceManagerException {
    // 250 quota units / user / second
    // each set of requests should assume they start with clean quota
    service = gmailServiceManager.signIn();
    Properties props = new Properties();
    session = Session.getInstance(props);
  }

  @Override
  public void signOut() throws GmailServiceManagerException {
    gmailServiceManager.signOut();
    reset();
  }

  @Override
  public void sendToServer(String contentDescription, String userEmail, String stackTraceText, String userText)
      throws IOException, InterruptedException {
    HttpClient.sendToServer(contentDescription, userEmail, stackTraceText, userText);
  }

  @Override
  public void subscribe(String emailAddress) throws IOException, InterruptedException {
    HttpClient.subscribe(emailAddress);
  }

  @Override
  public String getEmailAddress() throws GmailServiceException {
    if (emailAddress == null) {
      emailAddress = service.getEmailAddress();
    }
    return emailAddress;
  }

  @Override
  public LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings) {
    return new ProcessEmailTask(email, e -> processEmail(e, processSettings) /* 40 quota units */);
  }

  private ProcessEmailResult processEmail(Email email, ProcessSettings processSettings)
      throws IOException, MessagingException, GmailServiceException {
    Message message = service.getRawMessage(email.getGmailId()); // 5 quota units
//    System.out.println("===============================");
//    System.out.println(new ArrayList<>(message.keySet()));
//    System.out.println(message.toPrettyString());
//    TestStore.mergeMessage(message);
//    System.out.println("===============================");
    MimeMessage mimeMessage = getMimeMessage(message);
    String newUniqueId = null;
    if (processSettings.processOption.shouldBackup()) {
      backupEmail(email, processSettings, mimeMessage);
    }
    Set<String> fileNames = EmailProcessor.process(userStorage, email, mimeMessage, processSettings);
    if (processSettings.processOption.shouldDownload() && !processSettings.processOption.shouldRemove()) {
      service.addLabel(message.getId(), processSettings.processOption.getDownloadedLabelId());
    }
    if (processSettings.processOption.shouldRemove() && !fileNames.isEmpty()) {
      updateRawMessage(message, mimeMessage);
      Message newMessage = service.insertMessage(message); // 25 quota units
      System.out.println(new ArrayList<>(newMessage.keySet()));
      newMessage = service.getUniqueIdAndHeaders(newMessage); // 5 quota units
//      System.out.println("===============================");
//      System.out.println(new ArrayList<>(message.keySet()));
//      System.out.println(message.toPrettyString());
//      TestStore.mergeMessage(newMessage);
//      System.out.println("===============================");
      Map<String, String> headerMap = GmailUtils.getHeaderMap(newMessage);
      newUniqueId = headerMap.get("message-id");
      if (processSettings.processOption.shouldDownload()) {
        service.addLabel(newMessage.getId(), processSettings.processOption.getDownloadedLabelId());
      }
      service.addLabel(newMessage.getId(), processSettings.processOption.getRemovedLabelId());
      // 5-10 quota units
      service.deleteMessage(message.getId(), processSettings.processOption.shouldPermanentlyDeleteOriginal());
    }
    return new ProcessEmailResult(newUniqueId, fileNames);
  }

  private MimeMessage getMimeMessage(Message message) throws MessagingException, IOException {
    String rawBefore = message.getRaw();
    if (rawBefore == null) {
      throw new IOException("Unable to extract the contents of the email.");
    }
    byte[] emailBytes = decodeBase64(rawBefore);
    try (InputStream is = new ByteArrayInputStream(emailBytes)) {
      return new MimeMessage(session, is);
    }
  }

  private void backupEmail(Email email, ProcessSettings processSettings, MimeMessage mimeMessage)
          throws IOException, MessagingException {
    String filename = email.getGmailId() + ".eml";
    userStorage.saveMessage(mimeMessage, processSettings.targetDirectory, filename);
  }

  private void updateRawMessage(Message message, MimeMessage mimeMessage) throws IOException, MessagingException {
    try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      mimeMessage.writeTo(buffer);
      String raw = encodeBase64URLSafeString(buffer.toByteArray());
      message.setRaw(raw);
    }
  }

  @Override
  public GetEmailMetadataTask getSearchTask(String query) throws GmailServiceException {
    List<Message> messages = service.search(query);
    ArrayList<String> emailIdsToProcess =
        messages.stream().map(Message::getId).collect(Collectors.toCollection(ArrayList::new));

    JsonBatchCallback<Message> perEmailCallback = new JsonBatchCallback<>() {
      @Override
      public void onFailure(GoogleJsonError googleJsonError, HttpHeaders httpHeaders) throws IOException {
        throw new IOException(googleJsonError.getMessage());
      }

      @Override
      public void onSuccess(Message message, HttpHeaders httpHeaders) throws IOException {
//        System.out.println("===============================");
//        System.out.println(new ArrayList<>(message.keySet()));
//        TestStore.mergeMessage(message);
//        System.out.println("===============================");
        Map<String, String> headerMap = GmailUtils.getHeaderMap(message);
        String emailId = message.getId();
        String uniqueId = headerMap.get("message-id");
        List<String> labelIds = message.getLabelIds();
        String from = headerMap.get("from");
        String to = headerMap.get("to");
        String subject = headerMap.get("subject");
        long timestamp = message.getInternalDate();
        List<MessagePart> messageParts = message.getPayload().getParts();
        if (messageParts != null) { // Means, this is not a blank message
          List<String> attachments = messageParts.stream()
                  .map(MessagePart::getFilename).filter(StringUtils::isNotBlank).collect(Collectors.toList());
          Email email = new Email(emailId, uniqueId, labelIds, from, to, subject, timestamp, message.getSizeEstimate(),
                  attachments);
          searchResults.add(email);
        }
        else {
          LOGGER.log(Level.WARNING, "Skipping message as GMail returned no parts:\n" +
                  "\tGMail-ID: " + emailId + "\n" +
                  "\tMessage-ID: " + uniqueId + "\n" +
                  "\tFrom: " + from + "\n" +
                  "\tTo: " + to + "\n" +
                  "\tSubject: " + subject + "\n" +
                  "\tDate: " + new Date(timestamp));
        }
      }
    };

    return new GetEmailMetadataTask(emailIdsToProcess, (startIndexInclusive, endIndexExclusive) -> {
        List<String> emailIds = emailIdsToProcess.subList(startIndexInclusive, endIndexExclusive);
        service.batchGetMetadata(emailIds, perEmailCallback);
      }
    );
  }

  @Override
  public List<Email> getSearchResults() {
    return searchResults;
  }

  @Override
  public SortedMap<String, String> getIdToLabel() throws GmailServiceException {
    return service.getIdToLabel();
  }

  @Override
  public String createLabel(String name) throws GmailServiceException {
    Label labelIn = new Label();
    labelIn.setName(name);
    labelIn.setLabelListVisibility("labelShow");
    labelIn.setMessageListVisibility("show");
    LabelColor labelColor = new LabelColor();
    labelColor.setBackgroundColor("#ffffff");
    labelColor.setTextColor("#fb4c2f");
    labelIn.setColor(labelColor);
    Label labelOut = service.createLabel(labelIn);
    return labelOut.getId();
  }

  @Override
  public Config getConfig() {
    return config;
  }
}
