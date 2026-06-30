plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // permite a Room leer anotaciones en Kotlin y generar el código de la BD
}

android {
    namespace = "com.gemma.tensinini"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.gemma.tensinini"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Librería principal de Room para gestionar la base de datos SQLite
    implementation("androidx.room:room-runtime:2.7.1")
    // Permite usar Kapt para procesar las anotaciones de Room en Kotlin
    kapt("androidx.room:room-compiler:2.7.1")
    // Soporte de Room para Corrutinas (para hacer consultas sin congelar la pantalla)
    implementation("androidx.room:room-ktx:2.7.1")
    // DataStore para persistir el estado de la sesión de medición entre aperturas de la
    implementation ("androidx.datastore:datastore-preferences:1.1.1")
    //Navigation Compose para gestionar la navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:2.7.7")
}