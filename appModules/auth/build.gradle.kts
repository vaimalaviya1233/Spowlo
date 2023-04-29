@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.library")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization")
}
apply(plugin = "dagger.hilt.android.plugin")

android {
    namespace = "com.bobbyesp.appModules.auth"
    compileSdk = 33

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(":appModules:core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.compose)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.ext.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Librespot
    implementation("com.github.iTaysonLab.librespot-java:librespot-player:e95c4f0529:thin") {
        exclude(group = "xyz.gianlu.librespot", module = "librespot-sink")
        exclude(group = "com.lmax", module = "disruptor")
        exclude(group = "org.apache.logging.log4j")
    }

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.protobuf.converter)
    implementation(libs.retrofit.moshi.converter)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}