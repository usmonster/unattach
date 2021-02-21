package app.unattach.model;

import java.util.List;

public interface Config {
  int getEmailSize();
  boolean getDeleteOriginal();
  String getFilenameSchema();
  List<String> getLabelIds();
  String getDownloadedLabelId();
  String getRemovedLabelId();
  String getSearchQuery();
  boolean getSignInAutomatically();
  boolean getSubscribeToUpdates();
  String getTargetDirectory();
  void saveDownloadedLabelId(String downloadedLabelId);
  void saveEmailSize(int emailSize);
  void saveFilenameSchema(String schema);
  void saveLabelIds(List<String> labelIds);
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveSignInAutomatically(boolean signInAutomatically);
  void saveSubscribeToUpdates(boolean subscribeToUpdates);
  void saveTargetDirectory(String path);
  void setDeleteOriginal(boolean deleteOriginal);
}
