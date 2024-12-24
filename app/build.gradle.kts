plugins {
    id("com.android.library")
    id("maven-publish")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "vn.hnon.magicbottomnavigation"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"]) // Use Android's release variant
            }
            groupId = "vn.hnon.magicbottomnavigation"
            artifactId = "magicbottomnavigation"
            version = "0.0.1"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}