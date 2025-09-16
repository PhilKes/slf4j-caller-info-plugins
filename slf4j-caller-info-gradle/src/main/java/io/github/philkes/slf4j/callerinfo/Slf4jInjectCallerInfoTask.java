package io.github.philkes.slf4j.callerinfo;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.event.Level;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.philkes.slf4j.callerinfo.AddCallerInfoToLogsVisitor.INJECTED_METHOD_PATTERN;
import static io.github.philkes.slf4j.callerinfo.AddCallerInfoToLogsVisitor.SLF4J_LOGGER_FQN;

public abstract class Slf4jInjectCallerInfoTask extends DefaultTask {

  public Slf4jInjectCallerInfoTask() {
    setDescription("Adding caller-information to all SLF4J Log statements during compilation");
  }

  /**
   * Injected pattern, can include any conversion words ('%class','%line','%method')
   */
  @Input
  private String injection = "%class:%line";

  /**
   * Name of the used MDC parameter in the logging-pattern
   */
  @Input
  private String injectionMdcParameter = "callerInformation";


  /**
   * Whether or not to print the package-name of the class, if '%class' is present in 'injection' parameter
   */
  @Input
  private boolean includePackageName = false;

  /**
   * Target directory which contains the compiled '.class' files, defaults to project class target dir ('build/classes')
   */
  @Internal
  private File target = getProject().getLayout().getBuildDirectory().dir("classes").get().getAsFile();

  /**
   * Regex filters to only inject into specific Java classes
   */
  @Input
  private ClassFilters filters = ClassFilters.DEFAULT_FILTERS;

  /**
   * List of Regex to specify to which method calls the caller-information should be injected to.
   * <br/>
   * Defaults to all standard SLF4J {@link org.slf4j.Logger} methods.
   * Format: {@code <PACKAGE_PATH>/<CLASS_NAME>#<METHOD_NAME>}
   */
  @Input
  private List<String> injectedMethods = Arrays.stream(Level.values())
      .map(level -> String.format(INJECTED_METHOD_PATTERN, SLF4J_LOGGER_FQN,
          level.toString().toLowerCase()))
      .collect(Collectors.toList());

  @TaskAction
  public void inject() {
    Logger log = getLogger();
    if (injection.isEmpty() || (injectedMethods != null && injectedMethods.isEmpty())) {
      log.warn("'injection' or 'injectedMethods' is empty, skipping execution.");
      return;
    }
    String testFormat = injection;
    for (String conversionWord : io.github.philkes.slf4j.callerinfo.AddCallerInfoToLogsVisitor.CONVERSIONS) {
      testFormat = testFormat.replace(conversionWord, "");
    }
    if (testFormat.contains("%")) {
      log.warn("There is a `%` character in the 'injection' parameter," +
          " without a valid conversion word afterwards, the '%' will be printed in the log statement.");
      log.warn(String.format("Available conversion words: %s, current 'injection': %s",
          String.join(", ", AddCallerInfoToLogsVisitor.CONVERSIONS), injection));
    }

    log.lifecycle("Make sure to add MDC parameter '{}' to your logging pattern.",
        injectionMdcParameter);

    try {
      new CallerInfoLogsClassWriter(
          target,
          filters,
          injectionMdcParameter,
          injection,
          includePackageName,
          injectedMethods,
          new GradleLogger(log)
      ).execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // --- getters and setters (for Gradle property configuration) ---
  public String getInjection() {
    return injection;
  }

  public void setInjection(String injection) {
    this.injection = injection;
  }

  public String getInjectionMdcParameter() {
    return injectionMdcParameter;
  }

  public void setInjectionMdcParameter(String injectionMdcParameter) {
    this.injectionMdcParameter = injectionMdcParameter;
  }

  public boolean isIncludePackageName() {
    return includePackageName;
  }

  public void setIncludePackageName(boolean includePackageName) {
    this.includePackageName = includePackageName;
  }

  public File getTarget() {
    return target;
  }

  public void setTarget(File target) {
    this.target = target;
  }

  public ClassFilters getFilters() {
    return filters;
  }

  public void setFilters(ClassFilters filters) {
    this.filters = filters;
  }

  public List<String> getInjectedMethods() {
    return injectedMethods;
  }

  public void setInjectedMethods(List<String> injectedMethods) {
    this.injectedMethods = injectedMethods;
  }
}
