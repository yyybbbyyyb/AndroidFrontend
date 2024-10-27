plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
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

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit 和 Gson 依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp 依赖（可选但推荐）
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    implementation("com.google.android.material:material:1.9.0")

    implementation("com.squareup.picasso:picasso:2.8")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")

    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")

    implementation("com.kizitonwose.calendar:view:2.5.0")

    // 配置 desugaring 库
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}
