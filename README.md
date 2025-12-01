# slf4j-caller-info-plugins
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.philkes/slf4j-caller-info-maven-plugin/badge.svg)](https://mvnrepository.com/artifact/io.github.philkes/slf4j-caller-info-maven-plugin)
<a href="https://plugins.gradle.org/plugin/io.github.philkes.slf4j-caller-info"><img alt="Gradle Plugin Portal Version" src="https://img.shields.io/gradle-plugin-portal/v/io.github.philkes.slf4j-caller-info"></a>
[![Known Vulnerabilities](https://snyk.io/test/github/PhilKes/slf4j-caller-info-plugins/badge.svg)](https://snyk.io/test/github/PhilKes/slf4j-caller-info-plugins)
[![License](https://img.shields.io/badge/License-Apache--2.0-blue)](./LICENSE)

Maven and Gradle plugins to **inject caller-location-information** to all [SLF4J Logger](https://www.slf4j.org/api/org/slf4j/Logger.html) log statement invocations (`info()`, etc.) in your compiled code, as a better alternative to SLF4J caller location evaluation during runtime. Also allows to inject caller-information when using wrapper classes/methods (see [Configuration/injectedMethods](#configuration)).


## Description
By default SLF4J implementations such as `logback` or `log4j` offer to log the caller-location (e.g. [Logback/Layouts#method](https://logback.qos.ch/manual/layouts.html#method)), but this comes at a huge performance loss (see [Apache/Log4j2-Performance-Caller-Location](https://logging.apache.org/log4j/2.x/performance.html#asynchronous-logging-with-caller-location-information)). 

Instead of evaluating the caller-location (method, source code line number) during runtime, this plugin injects the caller-location info to all log statements when building the project.
The injection is done with a [MDC.put(...)](https://www.slf4j.org/api/org/slf4j/MDC.html#put-java.lang.String-java.lang.String-) call before every SLF4J log invocation, putting the class name, line number (optionally also method name) into the MDC in the compiled `.class` files. This allows to conveniently **print out where exactly in the code the log statement originates from** for every single log statement, without any overhead or performance loss, by simply adding the **Mapped Diagnostic Context** ([MDC](https://logback.qos.ch/manual/mdc.html)) parameter `callerInformation` to your logging-pattern configuration. It can therefore be used with any SLF4J implementation, such as [logback](https://logback.qos.ch/), [log4j2](https://logging.apache.org/log4j/2.x/), etc.

Since this plugin adds the necessary code during the build stage, there is **nearly no performance loss** by injecting the caller-location-information in comparison to using e.g. the `%class` or `%line` pattern parameters (see [Log4j2 manual](https://logging.apache.org/log4j/2.x/manual/layouts.html#Patterns) or [Logback manual](https://logback.qos.ch/manual/layouts.html#class) in your logging pattern, which look for the caller-information on the stacktrace during runtime which is very costly.

## Usage

### Maven
Add the Maven plugin to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.github.philkes</groupId>
            <artifactId>slf4j-caller-info-maven-plugin</artifactId>
            <version>1.1.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>inject</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

The plugin runs during `process-classes` or can be executed explicitly:
```shell
mvn slf4j-caller-info:inject
```

### Gradle
Apply the Gradle plugin in your build file.

Kotlin DSL (`build.gradle.kts`):
```kotlin
plugins {
    id("io.github.philkes.slf4j-caller-info") version "1.1.0"
}
```

The plugin automatically runs after `JavaCompile`. You can also execute the task directly:
```shell
./gradlew slf4InjectCallerInfo
```

Note: JDK 8 or higher required

## Code Example
See [logback.xml](slf4j-caller-info-maven/src/it/projects/logback/src/test/resources/logback.xml):
```xml
...
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <!-- Example log pattern including the needed 'callerInformation' MDC parameter -->
        <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} \(%X{callerInformation}\) - %msg%n</pattern>
    </encoder>
</appender>
...
```
See [LoggingTest.java](slf4j-caller-info-maven/src/it/projects/logback/src/main/java/io/github/philkes/slf4j/callerinfo/LoggingTest.java):
```java
1   package io.github.philkes.slf4j.callerinfo;
2
3   import org.slf4j.Logger;
4   import org.slf4j.LoggerFactory;
5
6   /**
7    * Example Class using an SLF4J Logger on different Levels
8    */
9   public class LoggingTest {
10      private final Logger log = LoggerFactory.getLogger(LoggingTest.class);
11
12      public void log(String msg) {
13          log.info(msg);
14          log.warn(msg);
15          log.error(msg);
16          log.debug(msg);
17          log.trace(msg);
18      }
19  }
```

Log Output of `LoggingTest.log("This is a test message")`:

<img src="./doc/log-output.png" width="800" style="border: 1px">

*(Screenshot from IntelliJ, automatically detects class + linenumber as links to jump directly into the source code)*

## Performance at runtime

<img src="./benchmark/results/results.png" width="500">

As you can see using the `slf4j-caller-info-maven-plugin` (orange bars) printing the caller location is about **4x faster** than using Log4j2's or Logback's caller-location built-in pattern (red bars). In total there is a performance loss of only ~9% for Log4j2 and ~8% for Logback in comparison to not logging the caller-location at all (blue bars).

System specs for the benchmarks:
JDK 17 on Linux Mint 20.3 with 8 cores CPU AMD Ryzen 2700X@3.7Ghz

The benchmarking was done with [JMH](https://github.com/openjdk/jmh) based on log4j's [log4j-perf](https://github.com/apache/logging-log4j2) module.
For more details about the benchmarks see the [benchmark](./benchmark/) module.


### Performance at compiletime
As for the time it takes the `inject` goal to execute, the compilation time of the plugin is evaluated by generating Java projects with n classes, that all contain 5 SLF4J log-statements and running the plugin on these projects and averaging the plugin's execution time.

<img src="./benchmark/results/results-compiletime.png" width="500">

We can see that the plugin's execution time obviously goes up the more classes and `SLF4J` log statements there are in the source code,
but even for a project with 10,000 classes the compilation time is ~2 seconds, which is only about twice as long as with only 1 class.


### Configuration
You have many optionally settings to further configure the plugin:
- injection: String pattern that may contain conversion words `%class`, `%line`, `%method` (default: `%class:%line`).
- injectionMdcParameter: MDC key name used in your logging pattern (default: `callerInformation`).
- includePackageName: Whether the package name should be printed when `%class` is used (default: `false`).
- filters: Include/Exclude regexes matching class files to inject into (defaults: includes `.*`, excludes empty).
- injectedMethods: Optional list of method descriptors to inject into. By default all standard SLF4J `Logger` methods are injected. You can add your own when using logging wrappers. Format: `<PACKAGE_PATH>/<CLASS_NAME>#<METHOD_NAME>`; method name may be a regex like `.*`.

#### Maven
```xml
<configuration>
  <!-- All parameters are optional; shown values are defaults -->
  <injection>%class:%line</injection>
  <injectionMdcParameter>callerInformation</injectionMdcParameter>
  <filters>
    <includes>
      <include>.*</include>
    </includes>
    <excludes/>
  </filters>
  <injectedMethods>
    <injectedMethod>org/slf4j/Logger#info</injectedMethod>
    <injectedMethod>org/slf4j/Logger#warn</injectedMethod>
    <injectedMethod>org/slf4j/Logger#error</injectedMethod>
    <injectedMethod>org/slf4j/Logger#debug</injectedMethod>
    <injectedMethod>org/slf4j/Logger#trace</injectedMethod>
  </injectedMethods>
  <includePackageName>false</includePackageName>
  <!-- Target directory containing compiled classes -->
  <target>${project.build.outputDirectory}</target>
</configuration>
```

#### Gradle (Kotlin DSL)
```kotlin
slf4jCallerInfo {
    // All parameters are optional; shown values are defaults
    injection = "%class:%line"
    injectionMdcParameter = "callerInformation"
    includePackageName = false

    // Limit injection to specific classes (regex on classfile path or name)
    filters = io.github.philkes.slf4j.callerinfo.ClassFilters().apply {
        includes = listOf(".*")
        excludes = listOf()
    }

    // Optionally override which methods to inject into (e.g., when using wrappers)
    // Format: <PACKAGE_PATH>/<CLASS_NAME>#<METHOD_NAME>
    // By default all SLF4J Logger methods (info,warn,error,debug,trace) are injected.
    // injectedMethods = listOf(
    //     "org/slf4j/Logger#info",
    //     "org/slf4j/Logger#warn",
    //     "org/slf4j/Logger#error",
    //     "org/slf4j/Logger#debug",
    //     "org/slf4j/Logger#trace",
    //     "io/github/yourapp/logging/LoggingWrapper#customLogMethod.*"
    // )

    // Advanced toggles
    // enabled = true
    // runAfterJavaCompile = true // plugin runs after JavaCompile by default
}
```

### Compiled .class File of Code Example
```java
// import ...
public class LoggingTest {
    //...
    public void log(String msg) {
        Logger var10000 = this.log;
        MDC.put("callerInformation", "LoggingTest.class:13");
        var10000.info(msg);
        MDC.remove("callerInformation");
        var10000 = this.log;
        MDC.put("callerInformation", "LoggingTest.class:14");
        var10000.warn(msg);
        ...
    }
}
```


## Dependencies
- [ASM](https://asm.ow2.io/) for Java bytecode manipulation
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/) for FileUtils
- Built with Java 17
- [JMH](https://github.com/openjdk/jmh) for benchmarks


This project is licensed under the terms of the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.txt).
