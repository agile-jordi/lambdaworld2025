plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("java-test-fixtures")
}

dependencies {

    // Implementation modules

    implementation(projects.modules.product)

    // Implementation libraries

    api(libs.arrow.core)
}
