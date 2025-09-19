plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {

    // Production libraries

    implementation(libs.arrow.core)

    // testImplementation libs
}
