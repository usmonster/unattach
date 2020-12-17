package app.unattach.view;

class IdLabel {
  private final String id;
  private final String label;

  IdLabel(String id, String label) {
    this.id = id;
    this.label = label;
  }

  String getId() {
    return id;
  }

  String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }
}
