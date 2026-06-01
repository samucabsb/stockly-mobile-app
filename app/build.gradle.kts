plugins { id("com.android.application") }

android {
    namespace = "br.com.samuel.stockly"
    compileSdk = 33
    defaultConfig {
        applicationId = "br.com.samuel.stockly.tutorial"
        minSdk = 23
        targetSdk = 33
        versionCode = 4
        versionName = "4.0-tutorial-offline"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
