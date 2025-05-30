plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("com.google.dagger.hilt.android") version "2.56.2"
    id("com.google.gms.google-services") //version "4.4.2" // apply false
}

//repositories{
//    mavenCentral()
//    google()
//}

android {
    namespace = "com.map711s.namibiahockey"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.map711s.namibiahockey"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            // Add Firebase debug logging
            buildConfigField("boolean", "FIREBASE_DEBUG", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "FIREBASE_DEBUG", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.androidx.material)
    implementation(libs.material3)
    implementation(libs.ui)
    implementation(libs.androidx.material)
    implementation("androidx.compose.material3:material3-window-size-class")

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.transport.runtime)
    implementation(libs.androidx.espresso.core)
    implementation(libs.transport.backend.cct)
    implementation(libs.transport.backend.cct)
    implementation(libs.androidx.runtime.saved.instance.state)
    implementation(libs.transport.runtime)
    ksp(libs.dagger.hilt.android.compiler)
    // implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.hilt.navigation.compose)
    // implementation(libs.androidx.hilt.navigation.fragment)
    // implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // ksp(libs.androidx.room.compiler)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // JSON Processing
    implementation(libs.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services) // For Firebase

    // Image loading - Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // For example, declare the dependencies for Firebase Authentication and Cloud Firestore
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.config.ktx) // Remote Config
    implementation(libs.firebase.dynamic.links.ktx) // For Deep Linking

    // Accompanist (Compose utilities)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.placeholder)

    // Date/Time picker
    implementation(libs.core)
    implementation(libs.calendar)
    implementation(libs.clock)

    //Android WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Maps integration
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    // The compose calendar library for Android
    implementation(libs.compose)

    // Pull to refresh
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Security
    implementation(libs.androidx.security.crypto.ktx)

    // Shimmer effect for loading
    implementation(libs.compose.shimmer)

    // For QR code generation/scanning
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore.core)

    implementation(libs.androidx.window)

    implementation(libs.coil.svg)
    implementation(libs.androidx.appcompat)
}