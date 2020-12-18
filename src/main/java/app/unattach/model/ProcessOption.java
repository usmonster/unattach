package app.unattach.model;

public class ProcessOption {
  private final boolean backup;
  private final boolean download;
  private final boolean remove;
  private final boolean deleteOriginal;
  private final String downloadedLabelId;
  private final String removedLabelId;

  public ProcessOption(boolean backup, boolean download, boolean remove, boolean deleteOriginal,
                       String downloadedLabelId, String removedLabelId) {
    this.backup = backup;
    this.download = download;
    this.remove = remove;
    this.deleteOriginal = deleteOriginal;
    this.downloadedLabelId = downloadedLabelId;
    this.removedLabelId = removedLabelId;
  }

  boolean shouldBackup() {
    return backup;
  }

  boolean shouldDownload() {
    return download;
  }

  boolean shouldRemove() {
    return remove;
  }

  boolean shouldDeleteOriginal() {
    return deleteOriginal;
  }

  public String getDownloadedLabelId() {
    return downloadedLabelId;
  }

  public String getRemovedLabelId() {
    return removedLabelId;
  }
}
