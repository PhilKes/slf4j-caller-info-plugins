package io.github.philkes.slf4j.callerinfo;

import org.gradle.api.tasks.compile.JavaCompile;

public class Slf4jCallerInfoExtension {
  /**
   * Whether or not to the plugin is enabled
   */
  private boolean enabled = true;

  /**
   * Whether or not to the {@link Slf4jInjectCallerInfoTask} task is run after the {@link JavaCompile} task
   */
  private boolean runAfterJavaCompile = true;

  /**
   * Injected pattern, can include any conversion words ('%class','%line','%method')
   */
  private String injection = "%class:%line";

  /**
   * Name of the used MDC parameter in the logging-pattern
   */
  private String injectionMdcParameter = "callerInformation";

  /**
   * Whether or not to print the package-name of the class, if '%class' is present in 'injection' parameter
   */
  private boolean includePackageName = false;

  /**
   * Regex filters to only inject into specific Java classes
   */
  private ClassFilters filters = ClassFilters.DEFAULT_FILTERS;

  /**
   * List of Regex to specify to which method calls the caller-information should be injected to.
   * Defaults to null which means use Task defaults (all standard SLF4J Logger methods)
   * Format: <PACKAGE_PATH>/<CLASS_NAME>#<METHOD_NAME>
   */
  private java.util.List<String> injectedMethods = null;

  /**
   * Gets the injected pattern, can include any conversion words ('%class','%line','%method')
   */
  public String getInjection() {
    return injection;
  }

  /**
   * Sets the injected pattern, can include any conversion words ('%class','%line','%method')
   */
  public void setInjection(String injection) {
    this.injection = injection;
  }

  /**
   * Gets the name of the used MDC parameter in the logging-pattern
   */
  public String getInjectionMdcParameter() {
    return injectionMdcParameter;
  }

  /**
   * Sets the name of the used MDC parameter in the logging-pattern
   */
  public void setInjectionMdcParameter(String injectionMdcParameter) {
    this.injectionMdcParameter = injectionMdcParameter;
  }
  /**
   * Gets whether or not to print the package-name of the class, if '%class' is present in 'injection' parameter
   */
  public boolean isIncludePackageName() {
    return includePackageName;
  }

  /**
   * Sets whether or not to print the package-name of the class, if '%class' is present in 'injection' parameter
   */
  public void setIncludePackageName(boolean includePackageName) {
    this.includePackageName = includePackageName;
  }

  /**
   * Gets the regex filters to only inject into specific Java classes
   */
  public ClassFilters getFilters() {
    return filters;
  }

  /**
   * Sets the regex filters to only inject into specific Java classes
   */
  public void setFilters(ClassFilters filters) {
    this.filters = filters;
  }

  /**
   * Gets custom injectedMethods list; null means use Task defaults
   */
  public java.util.List<String> getInjectedMethods() {
    return injectedMethods;
  }

  /**
   * Sets custom injectedMethods list; null means use Task defaults
   */
  public void setInjectedMethods(java.util.List<String> injectedMethods) {
    this.injectedMethods = injectedMethods;
  }

  /**
   * Gets whether or not to the plugin is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets whether or not to the plugin is enabled
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Gets whether or not to the {@link Slf4jInjectCallerInfoTask} task is run after the {@link JavaCompile} task
   */
  public boolean isRunAfterJavaCompile() {
    return runAfterJavaCompile;
  }

  /**
   * Sets whether or not to the {@link Slf4jInjectCallerInfoTask} task is run after the {@link JavaCompile} task
   */
  public void setRunAfterJavaCompile(boolean runAfterJavaCompile) {
    this.runAfterJavaCompile = runAfterJavaCompile;
  }
}
