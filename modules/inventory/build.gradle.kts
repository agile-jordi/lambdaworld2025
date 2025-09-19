plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {

    // API libraries

    api(libs.arrow.core)

    // Implementation modules

    implementation(projects.modules.product)
}
