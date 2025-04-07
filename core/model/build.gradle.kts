plugins {
    alias(libs.plugins.latticeonfhir.android.library)
    id("kotlin-parcelize")
}

dependencies {
    api(libs.kotlinx.datetime)

    implementation(libs.androidx.annotation.jvm)
}
