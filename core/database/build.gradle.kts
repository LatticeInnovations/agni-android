plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.android.room)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.core.database"
}

dependencies {
    api(projects.core.model)
    api(projects.core.sharedpreference)

    implementation(libs.kotlinx.datetime)
    implementation(libs.google.gson)
    implementation(libs.androidx.paging.runtime.ktx)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}