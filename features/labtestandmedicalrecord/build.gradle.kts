plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.features.labtestandmedicalrecord"
}

dependencies {
    api(projects.core.base)
    api(projects.core.model)
    api(projects.core.data)
    api(projects.core.utils)
    api(projects.core.ui)
    api(projects.core.navigation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.coil.kt.compose)
    implementation(libs.timber)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.iconsExtended)
}