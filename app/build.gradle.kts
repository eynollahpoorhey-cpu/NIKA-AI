plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseStorePath = System.getenv("NIKA_KEYSTORE_PATH")
val releaseStorePassword = System.getenv("NIKA_KEYSTORE_PASSWORD")
val releaseKeyAlias = System.getenv("NIKA_KEY_ALIAS")
val releaseKeyPassword = System.getenv("NIKA_KEY_PASSWORD")
val hasReleaseSigning = listOf(
    releaseStorePath, releaseStorePassword, releaseKeyAlias, releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.nika.ai"
    compileSdk = 35

    buildFeatures {\n        compose = true\n        buildConfig = true\n    }\n\n    val nikaApiBaseUrl = providers.gradleProperty("NIKA_API_BASE_URL").orElse("").get()\n\n    defaultConfig {
        applicationId = "com.nika.ai"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"\n        buildConfigField("String", "NIKA_API_BASE_URL", "\\\"$nikaApiBaseUrl\\\"")
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStorePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")\n    debugImplementation("androidx.compose.ui:ui-tooling")
}
