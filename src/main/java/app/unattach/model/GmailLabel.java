package app.unattach.model;

public record GmailLabel(String id, String name) implements Comparable<GmailLabel> {
  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(GmailLabel o) {
    return name.compareTo(o.name);
  }
}
