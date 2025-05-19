plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    alias(libs.plugins.latticeonfhir.android.library.jacoco)
    alias(libs.plugins.latticeonfhir.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.latticeonfhir.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)

    implementation(libs.androidx.annotation.jvm)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.room.ktx)
}
