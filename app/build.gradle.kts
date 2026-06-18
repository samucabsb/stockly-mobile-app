plugins {
    id("com.android.application")
}

android {
    namespace = "br.com.samuel.stockly"
    compileSdk = 33

    defaultConfig {
        applicationId = "br.com.samuel.stockly"
        minSdk = 23
        targetSdk = 33
        versionCode = 2
        versionName = "2.0.0-sem-perfis"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
