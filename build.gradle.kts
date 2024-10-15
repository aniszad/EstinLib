buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    //dagger hilt
    alias(libs.plugins.daggerHilt) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
}