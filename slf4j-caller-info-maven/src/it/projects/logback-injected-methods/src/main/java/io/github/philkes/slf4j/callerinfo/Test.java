package io.github.philkes.slf4j.callerinfo;

public class Test {
  private final LoggingWrapper loggingWrapper = new LoggingWrapper();

  public void log(String msg) {
    loggingWrapper.customLogMethod(msg);
    loggingWrapper.excludedMethod(msg);
    loggingWrapper.customLogMethod2(msg);
  }

  public LoggingWrapper getLoggingWrapper() {
    return loggingWrapper;
  }
}
