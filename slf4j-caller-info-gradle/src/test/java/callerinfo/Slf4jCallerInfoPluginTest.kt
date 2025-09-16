package callerinfo

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.io.path.toPath

class Slf4jCallerInfoPluginTest {

    @Test
    fun gradleIntegration_logbackDefault() {
        runFixture("logback-default")
    }

    @Test
    fun gradleIntegration_logbackFilters() {
        runFixture("logback-filters")
    }

    @Test
    fun gradleIntegration_logbackInjectedMethods() {
        runFixture("logback-injected-methods")
    }

    private fun runFixture(fixtureName: String) {
        val testProjectDir = javaClass.getResource("/$fixtureName")!!.toURI().toPath().toFile()
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("test", "--stacktrace")
            .forwardOutput() // show fixture build output in test logs
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":test")?.outcome)
    }

}