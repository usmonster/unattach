package app.unattach.model;

public class ProcessOption {
  private final boolean download;
  private final boolean remove;
  private final String labelId;

  public ProcessOption(boolean download, boolean remove) {
    this(download, remove, null);
  }

  public ProcessOption(boolean download, boolean remove, String labelId) {
    this.download = download;
    this.remove = remove;
    this.labelId = labelId;
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
