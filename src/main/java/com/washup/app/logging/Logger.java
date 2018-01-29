package com.washup.app.logging;

public class Logger {
  private final java.util.logging.Logger logger;

  private Logger(java.util.logging.Logger logger) {
    this.logger = logger;
  }

  public static Logger getLogger(Class<?> clazz) {
    return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
  }

  public void info(String msg, String ... args) {
    logger.info(String.format(msg, args));
  }

  public void warn(String msg, String ... args) {
    logger.warning(String.format(msg, args));
  }

  public void error(String msg, String ... args) {
    logger.severe(String.format(msg, args));
  }
}
