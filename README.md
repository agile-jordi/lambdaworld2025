# lambdaworld-2025

This project uses [Gradle](https://gradle.org/).
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew run` to build and run the application.
* Run `./gradlew build` to only build the application.
* Run `./gradlew check` to run all checks, including tests.
* Run `./gradlew spotlessApply` to format all the code.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`gradlew`). This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup. Modules are added to [
`settings.gradle.kts`](settings.gradle.kts).
The shared build logic was extracted to a convention plugin located in `buildSrc`.

This project uses a version catalog (see [`gradle/libs.versions.toml`](gradle/libs.versions.toml)) to declare and
version dependencies
and both a build cache and a configuration cache (see `gradle.properties`).

## Formatting code

This project is formatted using [ktfmt](https://github.com/facebook/ktfmt)
with [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle). The chosen style is `kotlinlangStyle`.

If using IntelliJ, install the [ktfmt plugin](https://plugins.jetbrains.com/plugin/14912-ktfmt) to replace the native
code formatter with ktfmt for this project and properly configure `kotlinlangStyle` in the IntelliJ project settings.

The Gradle build will fail if code is not properly formatted.

Run `./gradlew spotlessApply` to format all the code. 
