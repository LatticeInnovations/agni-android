//import org.gradle.testing.jacoco.tasks.JacocoReport
//
//plugins {
//    alias(libs.plugins.latticeonfhir.android.application)
//    alias(libs.plugins.latticeonfhir.android.application.compose)
//    alias(libs.plugins.latticeonfhir.android.application.flavors)
//    alias(libs.plugins.latticeonfhir.android.application.jacoco)
//    alias(libs.plugins.latticeonfhir.android.application.firebase)
//    alias(libs.plugins.latticeonfhir.hilt)
//    //    id("com.google.android.gms.oss-licenses-plugin")
//    alias(libs.plugins.baselineprofile)
//    alias(libs.plugins.roborazzi)
//    alias(libs.plugins.kotlin.serialization)
//    //    id 'com.android.application'
//    //    id 'org.jetbrains.kotlin.android'
//    //    id 'com.google.dagger.hilt.android'
//    //    id 'kotlin-parcelize'
//    //    id 'kotlin-android'
//    //    id 'kotlin-kapt'
//    //    id 'jacoco'
//    //    id 'com.google.gms.google-services'
//    //    id 'com.google.firebase.crashlytics'
//    //    id 'com.google.devtools.ksp'
//    //    id 'org.sonarqube' version("3.5.0.2730")
//}
//
//tasks.register<JacocoReport>("jacocoTestReport") {
//    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
//
//    reports {
//        xml.required.set(true)
//        html.required.set(true)
//    }
//
//    val fileFilter = listOf("**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "android/**/*.*")
//    val debugTree = fileTree(mapOf("dir" to "$buildDir/intermediates/classes/debug", "excludes" to fileFilter))
//    val mainSrc = "${project.projectDir}/src/main/java"
//
//    sourceDirectories.setFrom(files(listOf(mainSrc)))
//    classDirectories.setFrom(files(listOf(debugTree)))
//    executionData.setFrom(fileTree(mapOf("dir" to "$buildDir", "includes" to listOf(
//        "jacoco/testDebugUnitTest.exec",
//        "outputs/code-coverage/connected/*coverage.ec"
//    ))))
//}
//
//android {
//
//    namespace = "com.latticeonfhir.android"
//    compileSdk = 34
//
//    signingConfigs {
//        create("release") {
//            storeFile = file("../agni.jks")
//            storePassword = "latticeagni"
//            keyAlias = "key0"
//            keyPassword = "latticeagni"
//        }
//    }
//
//    defaultConfig {
//        applicationId = "com.latticeonfhir.android"
//        minSdk = 26
//        targetSdk = 34
//        versionCode = 68
//        versionName = "4.0.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//
//        ksp {
//            arg("room.schemaLocation", "$projectDir/schemas")
//        }
//
//        vectorDrawables {
//            useSupportLibrary = true
//        }
//    }
//
////    testOptions {
////        unitTests.all {
////            jacoco {
////                includeNoLocationClasses = true
////                excludes.add("jdk.internal.*")
////            }
////        }
////        unitTests.isReturnDefaultValues = true
////    }
//
//    buildTypes {
//        getByName("debug") {
//            isDebuggable = true
//            isMinifyEnabled = false
//            isShrinkResources = false
//            isTestCoverageEnabled = true
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            buildConfigField("String", "BASE_URL", "\"https://fhir.api.thelattice.org/facadeDev/api/v1/\"")
//            // dev
//        }
//        getByName("release") {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            buildConfigField("String", "BASE_URL", "\"https://fhir.api.thelattice.org/facadeDev/api/v1/\"")
//            // test
//            signingConfig = signingConfigs.getByName("release")
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//    kotlinOptions {
//        jvmTarget = "17"
//    }
//
//    buildFeatures {
//        viewBinding = true
//        dataBinding = true
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.14"
//    }
//    packagingOptions {
//        resources {
//            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
//        }
//    }
//    lint {
//        lintConfig = file("lint_ignore.xml")
//        disable("ParcelCreator")
//    }
//}
//
//dependencies {
//
//    //Android Core
//    implementation("androidx.core:core-ktx:1.13.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//
//    //Material Components
//    implementation("com.google.android.material:material:1.11.0")
//
//    //Constraint Layout
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//
//    //Testing
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    testImplementation("org.mockito:mockito-core:5.3.1")
//    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
//    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
//    testImplementation("androidx.arch.core:core-testing:2.2.0")
//    androidTestImplementation("org.mockito:mockito-android:3.10.0")
//    androidTestImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
//
//
//    //Recycler View
//    implementation("androidx.recyclerview:recyclerview:1.3.2")
//
//    //Hilt-Dagger
//    implementation("com.google.dagger:hilt-android:2.50")
//    kapt("com.google.dagger:hilt-compiler:2.50")
//
//    //Encrypt Shared Preferences
//    implementation("androidx.security:security-crypto:1.0.0")
//
//    //Room Database
//    implementation("androidx.room:room-runtime:2.6.1")
//    implementation("androidx.room:room-ktx:2.6.1")
//    ksp("androidx.room:room-compiler:2.6.1")
//
//    //Room Database Test Helpers
//    testImplementation("androidx.room:room-testing:2.6.1")
//
//    //Room Database Paging Support
//    implementation("androidx.room:room-paging:2.6.1")
//
//    //Paging 3
//    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
//
//    //Jetpack Compose Paging
//    implementation("androidx.paging:paging-compose:3.2.1")
//
//    //Paging 3 Test Implementation
//    testImplementation("androidx.paging:paging-common-ktx:3.2.1")
//    androidTestImplementation("androidx.paging:paging-common-ktx:3.2.1")
//
//    //Retrofit
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
//
//    //GSON
//    implementation("com.google.code.gson:gson:2.10.1")
//
//    //Fragment
//    implementation("androidx.fragment:fragment-ktx:1.6.2")
//
//    //SQL Cipher
//    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
//    implementation("androidx.sqlite:sqlite-ktx:2.4.0")
//
//    //Timber
//    implementation("com.jakewharton.timber:timber:5.0.1")
//
//    //compose
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.6")
//    debugImplementation("androidx.compose.ui:ui-tooling:1.6.6")
//    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.6")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
//    implementation("androidx.activity:activity-compose:1.9.0")
//    implementation("androidx.compose.ui:ui:1.6.6")
//    implementation("androidx.compose.ui:ui-tooling-preview:1.6.6")
//    implementation("androidx.compose.material3:material3:1.2.1")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//    implementation("androidx.compose.foundation:foundation:1.6.6")
//
//    // navigation
//    implementation("androidx.navigation:navigation-compose:2.7.7")
//
//    // date-time picker
//    implementation("com.marosseleng.android:compose-material3-datetime-pickers:0.6.1")
//
//    //Work Manager
//    implementation("androidx.work:work-runtime-ktx:2.9.0")
//    //Work Manager Testing
//    androidTestImplementation("androidx.work:work-testing:2.9.0")
//
//    //Fuzzy Wuzzy
//    implementation("me.xdrop:fuzzywuzzy:1.4.0")
//
//    // hilt- viewmodel compose
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//    // Firebase Bom
//    implementation(platform("com.google.firebase:firebase-bom:31.5.0"))
//
//    // Firebase Crashlytics
//    implementation("com.google.firebase:firebase-crashlytics-ktx")
//    implementation("com.google.firebase:firebase-analytics-ktx")
//
//    // splash screen
//    implementation("androidx.core:core-splashscreen:1.0.1")
//
//    // google consent api for auto detection of otp
//    implementation("com.google.android.gms:play-services-auth:21.1.0")
//    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.2")
//
//    // reordering in lazy column list
//    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
//
//    // glide
//    implementation("com.github.bumptech.glide:glide:4.14.2")
//
//    // cameraX
//    implementation("androidx.camera:camera-core:1.3.3")
//    implementation("androidx.camera:camera-camera2:1.3.3")
//    implementation("androidx.camera:camera-lifecycle:1.3.3")
//    implementation("androidx.camera:camera-view:1.3.3")
//
//    // accompanist
//    implementation("com.google.accompanist:accompanist-coil:0.15.0")
//
//    //mp chart
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//}
//
//allprojects {
//    configurations.configureEach {
//        resolutionStrategy.force("org.objenesis:objenesis:2.6")
//    }
//}
//
//sonar {
//    properties {
//        property("sonar.projectKey", "LatticeInnovations_FHIR-Android_AZAvReGoi3XM6MLXDS_L")
//        property("sonar.projectName", "FHIR-Android")
//        property("sonar.scm.provider", "git")
//    }
//}


/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.latticeonfhir.configs.NiaBuildType

plugins {
    alias(libs.plugins.latticeonfhir.android.application)
    alias(libs.plugins.latticeonfhir.android.application.compose)
    alias(libs.plugins.latticeonfhir.android.application.flavors)
    alias(libs.plugins.latticeonfhir.android.application.jacoco)
    alias(libs.plugins.latticeonfhir.android.application.firebase)
    alias(libs.plugins.latticeonfhir.hilt)
//    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.latticeonfhir.android"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = file("../agni.jks")
            storePassword = "latticeagni"
            keyAlias = "key0"
            keyPassword = "latticeagni"
        }
    }

    defaultConfig {
        applicationId = "com.latticeonfhir.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 68
        versionName = "4.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        vectorDrawables {
            useSupportLibrary = true
        }

        // Custom test runner to set up Hilt dependency graph
        testInstrumentationRunner = "com.google.samples.apps.latticeonfhir.core.testing.NiaTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = NiaBuildType.DEBUG.applicationIdSuffix
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            isTestCoverageEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://fhir.api.thelattice.org/facadeDev/api/v1/\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = NiaBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("release")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://fhir.api.thelattice.org/facadeDev/api/v1/\"")
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.theme)
    implementation(projects.app)
    implementation(projects.features.auth)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)
    implementation(project(":features:symptomsanddiagnosis"))

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlin.test)

    testDemoImplementation(libs.androidx.navigation.testing)
    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}

//dependencyGuard {
//    configuration("prodReleaseRuntimeClasspath")
//}