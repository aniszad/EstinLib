plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.daggerHilt)
    id("com.google.gms.google-services")


}

android {
    namespace = "com.az.elib"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.az.elib"
        minSdk = 29
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
    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES"
            )
        )
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation(libs.lottie)


    implementation(libs.jitpack.drive.library)

    // Swipe Refresh Layout
    implementation(libs.androidx.swiperefreshlayout)

    // view pager indicator
    implementation(libs.dotsindicator)


    //
    implementation (libs.google.auth.library.oauth2.http)

    implementation (libs.glide)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    annotationProcessor (libs.compiler)
    debugImplementation (libs.okhttp3.integration)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)



    // FIREBASE
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)


    // Image compression library
    implementation (libs.compressor)


    // HILT
    implementation(libs.hilt.android)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    kapt(libs.hilt.android.compiler)


    // SSP SDP
    implementation(libs.intuit.sdp)
    implementation(libs.intuit.ssp)
    implementation (libs.play.services.auth.v2020)

    implementation(libs.play.services.auth)
    implementation(libs.androidx.core.ktx)
    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
kapt {
    correctErrorTypes = true
}
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.grpc") {
            useVersion("1.57.2")
        }
    }
}