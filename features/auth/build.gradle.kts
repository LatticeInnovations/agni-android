plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.features.auth"
}

dependencies {
    api(projects.core.ui)
    api(projects.core.utils)
    api(projects.core.network)
    api(projects.core.navigation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.timber)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.iconsExtended)
}