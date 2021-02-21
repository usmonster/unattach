package app.unattach.controller;

import app.unattach.model.*;
import app.unattach.model.attachmentstorage.FakeUserStorage;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GmailServiceTest {
  private FakeUserStorage userStorage;
  private Controller controller;

  @BeforeEach
  public void setup() throws GmailServiceManagerException, GmailServiceException {
    try {
      userStorage = new FakeUserStorage();
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
  void testSearch(@TempDir Path tempDir) throws GmailServiceException, LongTaskException, IOException {
    Email email = searchForEmailsThroughController("simple attachment").get(0);
    // TODO: make it work with backupEmail and remove
    String downloadedLabelId = controller.getOrCreateDownloadedLabelId();
    String removedLabelId = controller.getOrCreateRemovedLabelId();
    ProcessOption processOption = new ProcessOption(Action.DOWNLOAD, false, true,
        downloadedLabelId, removedLabelId);
    String filenameSchema = "${ATTACHMENT_NAME}";
    ProcessSettings processSettings =
        new ProcessSettings(processOption, tempDir.toFile(), filenameSchema, true);
    LongTask<ProcessEmailResult> task = controller.getProcessTask(email, processSettings);
    ProcessEmailResult result = task.takeStep();
    assertNull(result.newUniqueId());
    assertEquals(Sets.newHashSet("logo-256.png"), result.filenames());
    Map<String, byte[]> subPathToAttachment = userStorage.getSubPathToAttachment();
    byte[] returnedBytes = subPathToAttachment.get("logo-256.png");
    byte[] targetBytes = FileUtils.readFileToByteArray(new File("test-store/logo-256.png"));
    assertArrayEquals(targetBytes, returnedBytes);
  }

  private List<Email> searchForEmailsThroughController(@SuppressWarnings("SameParameterValue") String query)
      throws GmailServiceException, LongTaskException {
    GetEmailMetadataTask searchTask = controller.getSearchTask(query);
    while (searchTask.hasMoreSteps()) {
      searchTask.takeStep();
    }
    return controller.getSearchResults();
  }
}