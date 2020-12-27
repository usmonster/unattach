package app.unattach.controller;

import app.unattach.model.*;
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
  Config getConfig();
  List<Email> getEmails();
  String getEmailAddress() throws IOException;
  SortedMap<String, String> getIdToLabel();
  DefaultArtifactVersion getLatestVersion();
  String getOrCreateDownloadedLabelId();
  String getOrCreateRemovedLabelId();
  LongTask<ProcessEmailResult> getProcessTask(Email email, ProcessSettings processSettings);
  GetEmailMetadataTask getSearchTask(String query) throws IOException, InterruptedException;
  void openFile(File file);
  void openQueryLanguagePage();
  void openUnattachHomepage();
  void openTermsAndConditions();
  void openWebPage(String uriString);
  String signIn() throws IOException, GeneralSecurityException;
  void signOut();
  void sendToServer(String contentDescription, String exceptionText, String userText);
  void subscribe(String emailAddress) throws Exception;
}
