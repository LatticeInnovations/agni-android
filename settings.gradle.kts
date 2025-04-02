import java.net.URI

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs = setOf(File("app/libs"))
        }
        maven { url = URI("https://jitpack.io") }
    }
}
rootProject.name = "FHIR-Android"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":theme")
include(":features:auth")
