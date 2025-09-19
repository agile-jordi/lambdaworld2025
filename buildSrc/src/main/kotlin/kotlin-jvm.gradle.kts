// The code in this file is a convention plugin - a Gradle
// mechanism for sharing reusable build
// logic.
// `buildSrc` is a Gradle-recognized directory and every
// plugin there will be easily available in
// the rest of the build.
package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin
    // in JVM projects.
    kotlin("jvm")
    id("com.diffplug.spotless")
}

kotlin {
    // Use a specific Java version to make it easier to work
    // in different environments.
    jvmToolchain(21)
    compilerOptions.optIn.addAll("kotlin.time.ExperimentalTime", "kotlin.uuid.ExperimentalUuidApi")
}

spotless { kotlin { ktfmt().kotlinlangStyle() } }

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.14")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.4.14")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    // Configure all test Gradle tasks to use JUnitPlatform.
    useJUnitPlatform()

    // Log information about all test results, not only the
    // failed ones.
    testLogging { events(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED) }
}
