plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.impostor.jogo"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.impostor.jogo"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
    }
    signingConfigs {
        create("impostor") {
            storeFile = file("impostor.jks")
            storePassword = "impostor123"
            keyAlias = "impostor"
            keyPassword = "impostor123"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("impostor")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
