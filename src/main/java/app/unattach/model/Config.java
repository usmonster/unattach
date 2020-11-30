package app.unattach.model;

public interface Config {
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
    void setDeleteOriginal(boolean deleteOriginal);
}
