plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    id("kotlin-parcelize")
}

android {
    namespace = "com.latticeonfhir.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)

    implementation(libs.androidx.annotation.jvm)
}
