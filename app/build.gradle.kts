plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Assinatura: o workflow passa a senha pelo segredo KEYSTORE_PASSWORD.
// Sem a senha (build local), o APK sai assinado com a chave de debug.
val keystorePassword: String? = System.getenv("KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() }

android {
    namespace = "com.impostor.jogo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.impostor.jogo"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "2.0"
    }

    // O jogo fica em /web. Ele entra no APK como cópia offline.
    sourceSets["main"].assets.srcDirs("../web")

    signingConfigs {
        if (keystorePassword != null) {
            create("release") {
                storeFile = rootProject.file("keystore/impostor-release.jks")
                storePassword = keystorePassword
                keyAlias = "impostor"
                keyPassword = keystorePassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (keystorePassword != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
