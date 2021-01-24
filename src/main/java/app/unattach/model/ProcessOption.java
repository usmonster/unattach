package app.unattach.model;

import app.unattach.view.Action;

public class ProcessOption {
  private final Action action;
  private final boolean backup;
  private final boolean download;
  private final boolean remove;
  private final boolean permanentlyDeleteOriginal;
  private final String downloadedLabelId;
  private final String removedLabelId;

  public ProcessOption(Action action, boolean backup, boolean download, boolean remove,
                       boolean permanentlyDeleteOriginal, String downloadedLabelId, String removedLabelId) {
    this.action = action;
    this.backup = backup;
    this.download = download;
    this.remove = remove;
    this.permanentlyDeleteOriginal = permanentlyDeleteOriginal;
    this.downloadedLabelId = downloadedLabelId;
    this.removedLabelId = removedLabelId;
  }

  public Action getAction() {
    return action;
  }

  boolean shouldBackup() {
    return backup;
  }

  public boolean shouldDownload() {
    return download;
  }

  public boolean shouldRemove() {
    return remove;
  }

  boolean shouldPermanentlyDeleteOriginal() {
    return permanentlyDeleteOriginal;
  }

  public String getDownloadedLabelId() {
    return downloadedLabelId;
  }

  public String getRemovedLabelId() {
    return removedLabelId;
  }
}
