plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.core.network"
}

dependencies {
    api(libs.kotlinx.datetime)
    api(projects.core.base)
    api(projects.core.model)
    api(projects.core.utils)
    api(projects.core.sharedpreference)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.timber)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)

    testImplementation(libs.kotlinx.coroutines.test)
}