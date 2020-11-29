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
  String getEmailAddress() throws IOException;
  SortedMap<String, String> getIdToLabel() throws IOException;
  List<Email> getEmails();
  String getFilenameSchema();
  LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings);
  String getRemovedLabelId();
  DefaultArtifactVersion getLatestVersion() throws IOException, InterruptedException;
  String getSearchQuery();
  String getTargetDirectory();
  int incrementNumberOfRuns();
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveTargetDirectory(String path);
  void signIn() throws IOException, GeneralSecurityException;
  void signOut() throws IOException;
  void sendToServer(String contentDescription, String userEmail, String stackTraceText, String userText)
      throws IOException, InterruptedException;
  void setFilenameSchema(String filenameSchema);
  void subscribe(String emailAddress) throws IOException, InterruptedException;
}
