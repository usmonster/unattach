package app.unattach.view;

class GmailLabel {
  private final String id;
  private final String name;

  GmailLabel(String id, String name) {
    this.id = id;
    this.name = name;
  }

  String getId() {
    return id;
  }

  String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
