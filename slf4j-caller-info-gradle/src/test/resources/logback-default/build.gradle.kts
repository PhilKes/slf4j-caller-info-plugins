plugins {
    java
    id("io.github.philkes.slf4j-caller-info") version "1.1.0"
}

group = "io.github.philkes"
version = "1.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.6")

    testImplementation("junit:junit:4.13.2")
}