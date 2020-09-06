package app.unattach.model;

public interface Config {
    String getFilenameSchema();
    String getRemovedLabelId();
    String getSearchQuery();
    String getTargetDirectory();
    void saveFilenameSchema(String schema);
    void saveRemovedLabelId(String removedLabelId);
    void saveSearchQuery(String query);
    void saveTargetDirectory(String path);
}
