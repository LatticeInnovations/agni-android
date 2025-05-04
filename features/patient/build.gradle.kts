plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.latticeonfhir.features.patient"
}

dependencies {
    api(projects.core.base)
    api(projects.core.model)
    api(projects.core.utils)
    api(projects.core.ui)
    api(projects.sync.workmanager)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime.android)
    implementation(libs.material3.android)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.work.ktx)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.timber)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.okhttp)
    implementation(libs.reorderable)
}