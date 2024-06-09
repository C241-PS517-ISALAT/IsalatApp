plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.isalatapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.isalatapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","AUTH_API_URL","\"https://sign-language-app-425005.et.r.appspot.com/\"")
        buildConfigField("String", "NEWS_API_KEY", "\"1d95acb225e74607a6a87efe6dd6347a\"")
        buildConfigField("String", "NEWS_API_URL", "\"https://newsapi.org/v2/\"")
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
    buildFeatures {
        viewBinding = true
        mlModelBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Material
    implementation(libs.material)
    //Exifinterface
    implementation(libs.androidx.exifinterface)
    //Retrofit & Okhttp3
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    //CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.video)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    //Glide
    implementation(libs.glide)
    //Data Store
    implementation(libs.androidx.datastore.preferences)
    //Live Data
    implementation(libs.androidx.lifecycle.viewmodel.ktx)


}