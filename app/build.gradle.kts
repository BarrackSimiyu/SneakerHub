plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.EliteEchelons"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.EliteEchelons"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.loopj.android:android-async-http:1.4.9")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("de.hdodenhof:circleimageview:3.0.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    implementation("com.google.code.gson:gson:2.8.9")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)

    // Firebase BOM and Messaging
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // Google Services
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Glide version compatible with dependencies
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.androidx.room.ktx)
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Force specific versions to avoid conflicts
configurations.all {
    resolutionStrategy {
        force(
            "androidx.annotation:annotation:1.6.0", // Ensure compatibility with your dependencies
            "androidx.core:core-ktx:1.10.0", // Ensure compatibility with your dependencies
            "androidx.appcompat:appcompat:1.6.0" // Ensure compatibility with your dependencies
        )
    }
}
