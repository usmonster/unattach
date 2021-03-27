package app.unattach.utils;

import static org.apache.log4j.Level.*;

public record Logger(org.apache.log4j.Logger logger) {
  /**
   * Passing this to the super calls ensures that the log lines are correct.
   */
  private static final String FQCN = Logger.class.getName();

  /**
   * @return A logger with the name of the class that called this method.
   */
  public static Logger get() {
    StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
    String callerClassName = stackTrace[1].getClassName();
    return get(callerClassName);
  }

  public static Logger get(String name) {
    return new Logger(org.apache.log4j.Logger.getLogger(name));
  }

  static Logger getRoot() {
    return new Logger(org.apache.log4j.Logger.getRootLogger());
  }

  public void debug(Object message) {
    logger.log(FQCN, DEBUG, message, null);
  }

  public void debug(String format, Object... args) {
    logger.log(FQCN, DEBUG, String.format(format, args), null);
  }

  public void info(Object message) {
    logger.log(FQCN, INFO, message, null);
  }

  public void info(String format, Object... args) {
    logger.log(FQCN, INFO, String.format(format, args), null);
  }

  public void warn(String format, Object... args) {
    logger.log(FQCN, WARN, String.format(format, args), null);
  }

  public void error(Object message) {
    logger.log(FQCN, ERROR, message, null);
  }

  public void error(String format, Object... args) {
    logger.log(FQCN, ERROR, String.format(format, args), null);
  }

  public void error(Object message, Throwable t) {
    logger.log(FQCN, ERROR, message, t);
  }
}
