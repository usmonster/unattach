package app.unattach.model;

import java.util.List;

public interface Config {
  int getEmailSize();
  boolean getDeleteOriginal();
  String getFilenameSchema();
  List<String> getLabelIds();
  String getRemovedLabelId();
  String getSearchQuery();
  String getTargetDirectory();
  int incrementNumberOfRuns();
  void saveFilenameSchema(String schema);
  void saveLabelIds(List<String> labelIds);
  void saveRemovedLabelId(String removedLabelId);
  void saveSearchQuery(String query);
  void saveTargetDirectory(String path);
  void saveEmailSize(int emailSize);
  void setDeleteOriginal(boolean deleteOriginal);
}
