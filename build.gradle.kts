// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Classpath for Google services plugin
        classpath("com.google.gms:google-services:4.3.14")
        // Note: Do not place your application dependencies here; they belong in the individual module 'build.gradle' files
    }
}

plugins {
    // Apply plugin versions in the root project, use the aliases for your modules
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
}
