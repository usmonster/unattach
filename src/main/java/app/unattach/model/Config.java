package app.unattach.model;

public interface Config {
  int getEmailSize();
  boolean getDeleteOriginal();
  String getFilenameSchema();
  String getRemovedLabelId();
  String getSearchQuery();
  String getTargetDirectory();
  int incrementNumberOfRuns();
  void saveFilenameSchema(String schema);
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveTargetDirectory(String path);
  void setEmailSize(int emailSize);
  void setDeleteOriginal(boolean deleteOriginal);
}
