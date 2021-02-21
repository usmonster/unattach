package app.unattach.view;

record GmailLabel(String id, String name) {
  @Override
  public String toString() {
    return name;
  }
}
