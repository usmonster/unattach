package app.unattach.model;

public class ProcessOption {
  private final boolean backup;
  private final boolean download;
  private final boolean remove;
  private final String labelId;

  public ProcessOption(boolean backup, boolean download, boolean remove) {
    this(backup, download, remove, null);
  }

  public ProcessOption(boolean backup, boolean download, boolean remove, String labelId) {
    this.backup = backup;
    this.download = download;
    this.remove = remove;
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

  public String getLabelId() {
    return labelId;
  }
}
