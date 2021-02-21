package app.unattach.controller;

import app.unattach.model.*;
import app.unattach.model.attachmentstorage.FileUserStorage;
import app.unattach.model.attachmentstorage.UserStorage;
import app.unattach.model.service.*;
import app.unattach.view.Action;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GmailServiceTest {
  private Controller controller;

  @BeforeEach
  public void setup() throws GmailServiceManagerException, GmailServiceException {
    try {
      UserStorage userStorage = new FileUserStorage();
      String emailAddress = "rok.strnisa@gmail.com";
      JsonFactory factory = JacksonFactory.getDefaultInstance();
      ListLabelsResponse listLabelsResponse = TestStore.loadLabels(factory);
      SortedMap<String, String> idToLabel = GmailService.labelsResponseToMap(listLabelsResponse);
      Message messageBefore = TestStore.loadMessage(factory, "1-simple-before");
      //noinspection ArraysAsListWithZeroOrOneArgument
      List<Message> messages = Arrays.asList(messageBefore);
      Map<String, String> beforeIdToAfterId = Map.of(messageBefore.getId(), "1-simple-after");
      GmailServiceManager gmailServiceManager =
          new FakeGmailServiceManager(emailAddress, idToLabel, messages, beforeIdToAfterId);
      Model model = new LiveModel(userStorage, gmailServiceManager);
      controller = new DefaultController(model);
      assertEquals(controller.signIn(), emailAddress);
    } catch (IOException e) {
      throw new GmailServiceManagerException(e);
    }
  }

  @Test
  void test_getOrCreateLabelId_SHOULD_work_WHEN_label_already_exists() {
    String downloadedLabelId = controller.getOrCreateDownloadedLabelId();
    assertEquals("Label_11", downloadedLabelId);
    String removedLabelId = controller.getOrCreateRemovedLabelId();
    assertEquals("Label_10", removedLabelId);
  }

  @Test
  void test_getSearchTask_SHOULD_work_WHEN_query_is_substring_of_subject() throws GmailServiceException,
      LongTaskException {
    List<Email> emails = searchForEmailsThroughController("simple attachment");
    assertEquals(1, emails.size());
  }

  @Test
  void test_getProcessTask_SHOULD_download_backup_and_not_modify_WHEN_downloading(@TempDir Path tempDir)
      throws GmailServiceException, LongTaskException, IOException {
    final String query = "simple attachment";
    List<ProcessEmailResult> results = processEmails(tempDir, query, Action.DOWNLOAD);

    // Check that there was only one result.
    assertEquals(1, results.size());
    ProcessEmailResult result = results.get(0);

    // Check that email ID hasn't changed and that the right attachments were found.
    HashSet<String> expectedAttachments = Sets.newHashSet("logo-256.png");
    assertNull(result.newUniqueId());
    assertEquals(expectedAttachments, result.filenames());

    // Check that the attachments were correctly backed up.
    Set<String> attachmentsFound = checkFilesEqual(tempDir.resolve("attachments"), Path.of("test-store"));
    assertEquals(expectedAttachments, attachmentsFound);

    // Check that an email backup was made.
    File[] emailBackups = getEmailBackups(tempDir);
    assertEquals(1, emailBackups.length);
    File emailBackup = emailBackups[0];

    // Check that the original email was not changed.
    File originalEmailBackup = tempDir.resolve(emailBackups[0].getName() + ".original").toFile();
    FileUtils.moveFile(emailBackup, originalEmailBackup);
    List<ProcessEmailResult> secondResults = processEmails(tempDir, query, Action.DOWNLOAD);
    assertEquals(1, secondResults.size());
    assertTrue(FileUtils.contentEquals(originalEmailBackup, emailBackup));
  }

  @Test
  void test_getProcessTask_SHOULD_download_backup_and_update_WHEN_downloading_and_deleting(@TempDir Path tempDir)
      throws GmailServiceException, LongTaskException, IOException, MessagingException {
    final String query = "simple attachment";
    List<ProcessEmailResult> results = processEmails(tempDir, query, Action.DOWNLOAD_AND_DELETE);

    // Check that there was only one result.
    assertEquals(1, results.size());
    ProcessEmailResult result = results.get(0);

    // Check that the email ID has changed and that right attachments were found.
    HashSet<String> expectedAttachments = Sets.newHashSet("logo-256.png");
    assertNotNull(result.newUniqueId());
    String newEmailId = result.newUniqueId();
    assertEquals(expectedAttachments, result.filenames());

    // Check that the attachments were correctly backed up.
    Set<String> attachmentsFound = checkFilesEqual(tempDir.resolve("attachments"), Path.of("test-store"));
    assertEquals(expectedAttachments, attachmentsFound);

    // Check that an email backup was made.
    File[] emailBackups = getEmailBackups(tempDir);
    assertEquals(1, emailBackups.length);
    File emailBackup = emailBackups[0];

    // Check that the original email was changed.
    File originalEmailBackup = tempDir.resolve(emailBackups[0].getName() + ".original").toFile();
    FileUtils.moveFile(emailBackup, originalEmailBackup);
    List<ProcessEmailResult> secondResults = processEmails(tempDir, query, Action.DOWNLOAD);
    assertEquals(1, secondResults.size());
    File[] secondEmailBackups = getEmailBackups(tempDir);
    assertEquals(1, secondEmailBackups.length);
    File newEmailBackup = secondEmailBackups[0];
    assertFalse(FileUtils.contentEquals(originalEmailBackup, newEmailBackup));

    // Sanity check the new email backup.
    assertTrue(newEmailBackup.length() < originalEmailBackup.length());
    Session session = Session.getInstance(new Properties());
    try (InputStream inputStream = new FileInputStream(newEmailBackup)) {
      MimeMessage newMimeMessage = new MimeMessage(session, inputStream);
      String content = getMainContent(newMimeMessage.getContent());
      assertTrue(content.contains("Previous attachments"));
    }
  }

  @SuppressWarnings("SameParameterValue")
  private List<ProcessEmailResult> processEmails(Path tempDir, String query, Action action)
      throws GmailServiceException, LongTaskException {
    List<ProcessEmailResult> results = new ArrayList<>();
    for (Email email : searchForEmailsThroughController(query)) {
      String downloadedLabelId = controller.getOrCreateDownloadedLabelId();
      String removedLabelId = controller.getOrCreateRemovedLabelId();
      ProcessOption processOption = new ProcessOption(action, true, true,
          downloadedLabelId, removedLabelId);
      String filenameSchema = "attachments/${ATTACHMENT_NAME}";
      ProcessSettings processSettings =
          new ProcessSettings(processOption, tempDir.toFile(), filenameSchema, true);
      LongTask<ProcessEmailResult> task = controller.getProcessTask(email, processSettings);
      results.add(task.takeStep());
    }
    return results;
  }

  private List<Email> searchForEmailsThroughController(@SuppressWarnings("SameParameterValue") String query)
      throws GmailServiceException, LongTaskException {
    GetEmailMetadataTask searchTask = controller.getSearchTask(query);
    while (searchTask.hasMoreSteps()) {
      searchTask.takeStep();
    }
    return controller.getSearchResults();
  }

  private Set<String> checkFilesEqual(Path directoryOfFilesToCheck, Path expectedFilesDirectory) throws IOException {
    File[] files = directoryOfFilesToCheck.toFile().listFiles();
    assertNotNull(files);
    for (File file : files) {
      File expectedFile = expectedFilesDirectory.resolve(file.getName()).toFile();
      assertTrue(FileUtils.contentEquals(expectedFile, file));
    }
    return Arrays.stream(files).map(File::getName).collect(Collectors.toSet());
  }

  private File[] getEmailBackups(@TempDir Path tempDir) {
    File[] files = tempDir.toFile().listFiles((dir, name) -> name.endsWith(".eml"));
    assertNotNull(files);
    return files;
  }

  private String getMainContent(Object content) throws MessagingException, IOException {
    if (content instanceof Multipart multipart) {
      for (int i = 0; i < multipart.getCount(); ++i) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        if (bodyPart.isMimeType("text/plain") || bodyPart.isMimeType("text/html")) {
          return bodyPart.getContent().toString();
        }
        String result = getMainContent(bodyPart.getContent());
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }
}