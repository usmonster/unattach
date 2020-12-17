package app.unattach.controller;

import app.unattach.model.Email;
import app.unattach.model.GetEmailMetadataTask;
import app.unattach.model.ProcessEmailResult;
import app.unattach.model.ProcessSettings;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.SortedMap;

public interface Controller {
  void clearPreviousSearch();
  String createLabel(String name);
  void donate(String item, int amount, String currency);
  boolean getDeleteOriginal();
  List<Email> getEmails();
  String getEmailAddress() throws IOException;
  int getEmailSize();
  SortedMap<String, String> getIdToLabel();
  String getFilenameSchema();
  List<String> getLabelIds();
  DefaultArtifactVersion getLatestVersion();
  String getOrCreateRemovedLabelId();
  LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings);
  String getRemovedLabelId();
  String getSearchQuery();
  GetEmailMetadataTask getSearchTask(String query) throws IOException, InterruptedException;
  String getTargetDirectory();
  int incrementNumberOfRuns();
  void openFile(File file);
  void openQueryLanguagePage();
  void openUnattachHomepage();
  void openTermsAndConditions();
  void openWebPage(String uriString);
  void saveEmailSize(int emailSize);
  void saveFilenameSchema(String filenameSchema);
  void saveLabelIds(List<String> labelIds);
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveTargetDirectory(String path);
  String signIn() throws IOException, GeneralSecurityException;
  void signOut();
  void sendToServer(String contentDescription, String exceptionText, String userText);
  void setDeleteOriginal(boolean deleteOriginal);
  void subscribe(String emailAddress) throws Exception;
}
