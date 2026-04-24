plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("androidx.room")
    //id("com.google.dagger.hilt.android")
    //alias(libs.plugins.hilt.android)
    //alias(libs.plugins.ksp)
    //alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.example.sovereignledger"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.sovereignledger"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")

//    implementation("com.google.dagger:hilt-android:2.51.1")
//    ksp("com.google.dagger:hilt-compiler:2.51.1")

    //coil for image loading
//    implementation(libs.coil.compose)
//
//    // --- Networking ---
//    // Retrofit: Type-safe HTTP client for API calls
//    implementation(libs.retrofit)
//    // Moshi converter for Retrofit to handle JSON
//    implementation(libs.converter.moshi)
//    // OkHttp logging interceptor for debugging network requests
//    implementation(libs.logging.interceptor)
//    // OkHttp core networking library
//    implementation(libs.okhttp)
//    // Moshi: Modern JSON library for Kotlin
//    implementation(libs.moshi.kotlin)
//    // Moshi code generation for performance (uses KSP)
//    "ksp"(libs.moshi.kotlin.codegen)
//    implementation(libs.ml.kit.face.detection)
//    // --- CameraX ---
//    // CameraX core library
//    implementation(libs.androidx.camera.core)
//    // Camera2 implementation for CameraX
//    implementation(libs.androidx.camera.camera2)
//    // Integration with Android Lifecycles (auto start/stop)
//    implementation(libs.androidx.camera.lifecycle)
//    // UI components for Camera (PreviewView)
//    implementation(libs.androidx.camera.view)
//
//    implementation(libs.accompanist.permissions)

    // CameraX core libraries
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

// ML Kit Face Detection
    implementation("com.google.mlkit:face-detection:16.1.6")

    implementation(libs.androidx.datastore.preferences)

    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.compose.ui:ui-text-google-fonts")

    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.mlkit:face-detection:16.1.6")

}

room{
    schemaDirectory("$projectDir/schemas")
}