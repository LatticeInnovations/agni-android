plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
}

android {
    namespace = "com.latticeonfhir.features.vaccination"
}

dependencies {
    api(projects.core.base)
    api(projects.core.utils)
    api(projects.core.ui)
    api(projects.core.model)
    api(projects.core.theme)
    api(projects.core.navigation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.foundation.android)
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.iconsExtended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.coil.kt.compose)
    implementation(libs.timber)
}