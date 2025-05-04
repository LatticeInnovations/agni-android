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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "FHIR-Android"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:model")
include(":core:database")
include(":core:sharedpreference")
include(":core")
include(":core:base")
include(":core:network")
include(":core:data")
include(":features:symptomsanddiagnosis")
include(":core:utils")
include(":core:ui")
include(":core:theme")
include(":features:cvd")
include(":features:auth")
include(":features:vaccination")
include(":features:prescription")
include(":features:dispense")
include(":features:appointment")
include(":features:patient")
include(":sync")
include(":features:vitals")
include(":sync:workmanager")