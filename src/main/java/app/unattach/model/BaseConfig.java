package app.unattach.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class BaseConfig implements Config {
  private static final String DELETE_ORIGINAL_PROPERTY = "delete_original";
  private static final String EMAIL_SIZE_PROPERTY = "email_size";
  private static final String LABEL_IDS_PROPERTY = "label_ids";
  private static final String FILENAME_SCHEMA_PROPERTY = "filename_schema";
  private static final String NUMBER_OF_RUNS_PROPERTY = "number_of_runs";
  private static final String DOWNLOADED_LABEL_ID_PROPERTY = "downloaded_label_id";
  private static final String REMOVED_LABEL_ID_PROPERTY = "removed_label_id";
  private static final String SEARCH_QUERY_PROPERTY = "search_query";
  private static final String TARGET_DIRECTORY_PROPERTY = "target_directory";

  protected final Properties config;

  public BaseConfig() {
    config = new Properties();
    loadConfig();
  }

  public void loadConfig() {}

  public void saveConfig() {}

  @Override
  public int getEmailSize() {
    return Integer.parseInt(config.getProperty(EMAIL_SIZE_PROPERTY, "1"));
  }

  @Override
  public boolean getDeleteOriginal() {
    return Boolean.parseBoolean(config.getProperty(DELETE_ORIGINAL_PROPERTY, "true"));
  }

  @Override
  public String getFilenameSchema() {
    return config.getProperty(FILENAME_SCHEMA_PROPERTY, FilenameFactory.DEFAULT_SCHEMA);
  }

  @Override
  public List<String> getLabelIds() {
    return Arrays.asList(config.getProperty(LABEL_IDS_PROPERTY, "").split(","));
  }

  private int getNumberOfRuns() {
    return Integer.parseInt(config.getProperty(NUMBER_OF_RUNS_PROPERTY, "0"));
  }

  @Override
  public String getDownloadedLabelId() {
    return config.getProperty(DOWNLOADED_LABEL_ID_PROPERTY);
  }

  @Override
  public String getRemovedLabelId() {
    return config.getProperty(REMOVED_LABEL_ID_PROPERTY);
  }

  @Override
  public String getSearchQuery() {
    return config.getProperty(SEARCH_QUERY_PROPERTY, "has:attachment size:1m");
  }

  @Override
  public String getTargetDirectory() {
    return config.getProperty(TARGET_DIRECTORY_PROPERTY, getDefaultTargetDirectory());
  }

  @Override
  public int incrementNumberOfRuns() {
    int numberOfRuns = getNumberOfRuns() + 1;
    config.setProperty(NUMBER_OF_RUNS_PROPERTY, Integer.toString(numberOfRuns));
    saveConfig();
    return numberOfRuns;
  }

  @Override
  public void saveFilenameSchema(String schema) {
    config.setProperty(FILENAME_SCHEMA_PROPERTY, schema);
    saveConfig();
  }

  @Override
  public void saveLabelIds(List<String> labelIds) {
    config.setProperty(LABEL_IDS_PROPERTY, String.join(",", labelIds));
    saveConfig();
  }

  @Override
  public void saveDownloadedLabelId(String downloadedLabelId) {
    config.setProperty(DOWNLOADED_LABEL_ID_PROPERTY, downloadedLabelId);
    saveConfig();
  }

  @Override
  public void saveRemovedLabelId(String removedLabelId) {
    config.setProperty(REMOVED_LABEL_ID_PROPERTY, removedLabelId);
    saveConfig();
  }

  @Override
  public void saveSearchQuery(String query) {
    config.setProperty(SEARCH_QUERY_PROPERTY, query);
    saveConfig();
  }

  @Override
  public void saveTargetDirectory(String path) {
    config.setProperty(TARGET_DIRECTORY_PROPERTY, path);
    saveConfig();
  }

  @Override
  public void saveEmailSize(int emailSize) {
    config.setProperty(EMAIL_SIZE_PROPERTY, Integer.toString(emailSize));
    saveConfig();
  }

  @Override
  public void setDeleteOriginal(boolean deleteOriginal) {
    config.setProperty(DELETE_ORIGINAL_PROPERTY, Boolean.toString(deleteOriginal));
    saveConfig();
  }

  private static String getDefaultTargetDirectory() {
    String userHome = System.getProperty("user.home");
    Path defaultPath = Paths.get(userHome, "Downloads", Constants.PRODUCT_NAME);
    return defaultPath.toString();
  }
}
