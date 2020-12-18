package app.unattach.view;

public enum Action {
  DOWNLOAD("download"),
  DELETE("delete"),
  DOWNLOAD_AND_DELETE("download and delete");

  private final String name;

  Action(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
