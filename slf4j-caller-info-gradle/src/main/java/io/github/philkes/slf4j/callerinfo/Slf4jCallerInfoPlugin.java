package io.github.philkes.slf4j.callerinfo;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;

public class Slf4jCallerInfoPlugin implements Plugin<Project> {

  public static final String INJECT_TASK_NAME = "slf4InjectCallerInfo";
  public static final String EXTENSION_NAME = "slf4jCallerInfo";

  @Override
  public void apply(Project target) {
    final Slf4jCallerInfoExtension extension = target.getExtensions()
        .create(EXTENSION_NAME, Slf4jCallerInfoExtension.class);
    TaskProvider<Slf4jInjectCallerInfoTask> injectTask = target.getTasks()
        .register(INJECT_TASK_NAME, Slf4jInjectCallerInfoTask.class, task -> {
          task.setEnabled(extension.isEnabled());
          task.setInjection(extension.getInjection());
          task.setInjectionMdcParameter(extension.getInjectionMdcParameter());
          task.setIncludePackageName(extension.isIncludePackageName());
          task.setFilters(extension.getFilters());
          if (extension.getInjectedMethods() != null) {
            task.setInjectedMethods(extension.getInjectedMethods());
          }
        });
    target.afterEvaluate(p -> {
      if (extension.isRunAfterJavaCompile()) {
        target.getTasks().withType(JavaCompile.class).configureEach(javaCompile -> {
          javaCompile.finalizedBy(injectTask);
        });
      }
    });
  }

}
