package app.unattach.model;

import java.util.Set;

public class ProcessEmailResult {
  private final String newUniqueId;
  private final Set<String> filenames;

  ProcessEmailResult(String newUniqueId, Set<String> filenames) {
    this.newUniqueId = newUniqueId;
    this.filenames = filenames;
  }

  public String getNewUniqueId() {
    return newUniqueId;
  }
}
