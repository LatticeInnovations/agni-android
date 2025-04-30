plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.core.data"
}

dependencies {
    api(projects.core.database)
    api(projects.core.sharedpreference)
    api(projects.core.utils)

    implementation(libs.timber)
    implementation(libs.androidx.paging.runtime.ktx)
}