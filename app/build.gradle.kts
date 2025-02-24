plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.testmlkitocr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.testmlkitocr"
        minSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // 명시적 버전으로 androidx 라이브러리 설정
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1") // compileSdkVersion 34과 호환
    implementation("androidx.activity:activity:1.7.2") // compileSdkVersion 34과 호환
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") // compileSdkVersion 34과 호환되는 생명주기


    // 테스트 라이브러리
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 라틴 문자 (영어 등) 인식
    implementation ("com.google.mlkit:text-recognition:16.0.1")

    // 한국어 문자 인식
    implementation ("com.google.mlkit:text-recognition-korean:16.0.1")

    // 이미지 크롭
    implementation ("com.github.yalantis:ucrop:2.2.10")

}