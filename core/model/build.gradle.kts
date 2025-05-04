plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    id("kotlin-parcelize")
}

android {
    namespace = "com.latticeonfhir.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)
    api(projects.core.database)

    implementation(libs.androidx.annotation.jvm)
    implementation(libs.retrofit.converter.gson)
}
