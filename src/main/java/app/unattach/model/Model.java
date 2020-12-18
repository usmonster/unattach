package app.unattach.model;

import app.unattach.controller.LongTask;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.SortedMap;

public interface Model {
  void clearPreviousSearch();
  String createLabel(String name) throws IOException;
  GetEmailMetadataTask getSearchTask(String query) throws IOException, InterruptedException;
  boolean getDeleteOriginal();
  String getEmailAddress() throws IOException;
  SortedMap<String, String> getIdToLabel() throws IOException;
  List<Email> getEmails();
  int getEmailSize();
  String getFilenameSchema();
  List<String> getLabelIds();
  LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings);
  String getDownloadedLabelId();
  String getRemovedLabelId();
  DefaultArtifactVersion getLatestVersion() throws IOException, InterruptedException;
  String getSearchQuery();
  String getTargetDirectory();
  int incrementNumberOfRuns();
  void saveLabelIds(List<String> labelIds);
  void saveDownloadedLabelId(String downloadedLabelId);
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveTargetDirectory(String path);
  void setDeleteOriginal(boolean deleteOriginal);
  void signIn() throws IOException, GeneralSecurityException;
  void signOut() throws IOException;
  void saveEmailSize(int emailSize);
  void setFilenameSchema(String filenameSchema);
  void sendToServer(String contentDescription, String userEmail, String stackTraceText, String userText)
      throws IOException, InterruptedException;
  void subscribe(String emailAddress) throws IOException, InterruptedException;
}
