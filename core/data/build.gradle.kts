plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(projects.core.database)
    api(projects.core.utils)
}