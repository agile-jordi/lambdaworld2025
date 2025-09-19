plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

dependencies {

    // Production modules

    implementation(projects.modules.inventory)
    implementation(projects.modules.product)

    // implementation libraries

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.test.host)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Test implementation modules

    testImplementation(testFixtures(projects.modules.product))
    testImplementation(testFixtures(projects.modules.inventory))

    // testImplementation libs

    testImplementation(libs.kotest.runner)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.kotlinx.datetime)
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "com.agilogy.lambdaworld2025.inventory.api.AppKt"
}
