plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.1.0"
}

android {
    namespace = "ru.ikom.androiddecomposeviews"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.ikom.androiddecomposeviews"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.fragment:fragment-ktx:1.8.5")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    implementation("com.arkivanov.decompose:decompose:3.2.2")
    implementation("com.arkivanov.decompose:extensions-android:3.2.2")
    implementation("com.arkivanov.essenty:lifecycle:2.4.0")
    implementation("com.arkivanov.essenty:lifecycle-coroutines:2.1.0")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}