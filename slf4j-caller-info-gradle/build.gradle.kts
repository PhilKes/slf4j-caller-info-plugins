import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.3.0"
    kotlin("jvm") version "2.2.0"
}

group = "io.github.philkes"
version = "1.1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.philkes:slf4j-caller-info-core:${version}")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("commons-io:commons-io:2.13.0")
    implementation("org.slf4j:slf4j-ext:1.7.36")

    testImplementation("junit:junit:4.13.2")
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation(kotlin("stdlib-jdk8"))
}

gradlePlugin {
    website = "https://github.com/PhilKes/slf4j-caller-info-plugins"
    vcsUrl = "https://github.com/PhilKes/slf4j-caller-info-plugins"
    description = "Adding caller-information to all SLF4J Log statements during compilation"
    plugins {
        create("slf4j-caller-info") {
            id = "io.github.philkes.slf4j-caller-info"
            displayName = "SLF4J Caller-Information Injection"
            description = "Adding caller-information to all SLF4J Log statements during compilation"
            tags = listOf("slf4j", "caller-information", "asm")
            implementationClass = "io.github.philkes.slf4j.callerinfo.Slf4jCallerInfoPlugin"
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(8)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.test {
    useJUnitPlatform()
    dependsOn("publishToMavenLocal")
}