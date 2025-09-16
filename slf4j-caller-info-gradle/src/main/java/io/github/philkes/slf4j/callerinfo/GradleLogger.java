package io.github.philkes.slf4j.callerinfo;

import org.gradle.api.logging.Logger;

public class GradleLogger implements io.github.philkes.slf4j.callerinfo.Logger {

  private final Logger log;

  public GradleLogger(Logger log) {
    this.log = log;
  }

  @Override
  public void debug(String msg) {
    log.debug(msg);
  }

  @Override
  public void info(String msg) {
    log.lifecycle(msg);
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
