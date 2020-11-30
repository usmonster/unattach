package app.unattach.model;

public class ProcessOption {
  private final boolean backup;
  private final boolean download;
  private final boolean remove;
  private final boolean deleteOriginal;
  private final String labelId;

  public ProcessOption(boolean backup, boolean download, boolean remove) {
    this(backup, download, remove, false, null);
  }

  public ProcessOption(boolean backup, boolean download, boolean remove, boolean deleteOriginal, String labelId) {
    this.backup = backup;
    this.download = download;
    this.remove = remove;
    this.deleteOriginal = deleteOriginal;
    this.labelId = labelId;
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

  public String getLabelId() {
    return labelId;
  }
}
