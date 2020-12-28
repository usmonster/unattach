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
  boolean getSubscribeToUpdates();
  String getTargetDirectory();
  int incrementNumberOfRuns();
  void saveDownloadedLabelId(String downloadedLabelId);
  void saveEmailSize(int emailSize);
  void saveFilenameSchema(String schema);
  void saveLabelIds(List<String> labelIds);
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveTargetDirectory(String path);
  void saveSubscribeToUpdates(boolean subscribeToUpdates);
  void setDeleteOriginal(boolean deleteOriginal);
}
