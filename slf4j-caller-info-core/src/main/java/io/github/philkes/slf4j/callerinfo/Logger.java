package io.github.philkes.slf4j.callerinfo;

public interface Logger {
    void debug(String msg);
    void info(String msg);
    void warn(String msg);
    void error(String msg);
}
