package app.unattach.view;

class SchedulePeriod {
  private final String name;
  private final int seconds;

  SchedulePeriod(String name, int seconds) {
    this.name = name;
    this.seconds = seconds;
  }

  String getName() {
    return name;
  }

  int getSeconds() {
    return seconds;
  }

  @Override
  public String toString() {
    return name;
  }
}
