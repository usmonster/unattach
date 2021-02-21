package app.unattach.model;

import app.unattach.view.Action;

public record ProcessOption(Action action, boolean backupEmail, boolean permanentlyDeleteOriginal,
                            String downloadedLabelId, String removedLabelId) {
  public boolean shouldDownload() {
    return action == Action.DOWNLOAD || action == Action.DOWNLOAD_AND_DELETE;
  }

  public boolean shouldRemove() {
    return action == Action.DELETE || action == Action.DOWNLOAD_AND_DELETE;
  }
}
