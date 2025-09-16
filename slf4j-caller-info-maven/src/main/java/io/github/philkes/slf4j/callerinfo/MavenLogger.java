package io.github.philkes.slf4j.callerinfo;

import org.apache.maven.plugin.logging.Log;

public class MavenLogger implements Logger {

  private final Log log;

  public MavenLogger(Log log) {
    this.log = log;
  }

  @Override
  public void debug(String msg) {
    log.debug(msg);
  }

  @Override
  public void info(String msg) {
    log.info(msg);
  }

  @Override
  public void warn(String msg) {
    log.warn(msg);
  }

  @Override
  public void error(String msg) {
    log.error(msg);
  }
}
