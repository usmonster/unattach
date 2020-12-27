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
  Config getConfig();
  GetEmailMetadataTask getSearchTask(String query) throws IOException, InterruptedException;
  String getEmailAddress() throws IOException;
  SortedMap<String, String> getIdToLabel() throws IOException;
  List<Email> getEmails();
  LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings);
  DefaultArtifactVersion getLatestVersion() throws IOException, InterruptedException;
  void signIn() throws IOException, GeneralSecurityException;
  void signOut() throws IOException;
  void sendToServer(String contentDescription, String userEmail, String stackTraceText, String userText)
      throws IOException, InterruptedException;
  void subscribe(String emailAddress) throws IOException, InterruptedException;
}
