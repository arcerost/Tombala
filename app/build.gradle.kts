plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "extrydev.app.tombalam"
    compileSdk = 34

    defaultConfig {
        applicationId = "extrydev.app.tombalam"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = 2
        versionName = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packaging {
        resources {
            pickFirsts.add("mozilla/public-suffix-list.txt")
            excludes.add("META-INF/io.netty.versions.properties")
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {

    //noinspection GradleDependency
    implementation("androidx.core:core-ktx:1.10.1")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    //noinspection GradleDependency
    implementation("androidx.compose.foundation:foundation:1.5.0-beta01")

    // AWS
    implementation("com.amplifyframework:aws-auth-cognito:1.17.0")
    implementation("com.amazonaws:aws-android-sdk-mobile-client:2.22.2@aar")
    implementation ("com.amazonaws:aws-android-sdk-auth-userpools:2.22.2@aar")
    implementation ("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.22.2")
    implementation ("com.amazonaws:aws-android-sdk-sns:2.7.6")
    implementation ("com.amazonaws:aws-android-sdk-core:2.22.2")
    implementation ("org.bitbucket.b_c:jose4j:0.7.9")
    implementation ("software.amazon.awssdk:s3:2.20.82")
    implementation ("com.amazonaws:aws-android-sdk-s3:2.22.2")

    // Navigation
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // Retrofit & OkHttp
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Coroutines & LiveData
    // -----------------------
    //noinspection GradleDependency
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    //noinspection GradleDependency
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    //noinspection GradleDependency
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.0-beta01")

    // WebSocket & Firebase
    implementation ("org.java-websocket:Java-WebSocket:1.5.1")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    //noinspection GradleDependency
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation ("com.google.firebase:firebase-messaging:23.2.1")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    // DataStore, Coil, Google Maps/Fonts & Animations
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-gif:2.2.2")

    //Google Fonts
    //noinspection GradleDependency
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.4.3")

    //bottom-bar
    implementation ("com.exyte:animated-navigation-bar:1.0.0")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Room
    val roomVersion = "2.5.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.5.2")

    //Lifecycle
    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    //Constraint Layout
    //noinspection GradleDependency
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha08")

    //Ktor
    implementation ("io.ktor:ktor-client-core:2.3.4")
    implementation ("io.ktor:ktor-client-websockets:2.3.4")


    //effects
    implementation ("nl.dionsegijn:konfetti-compose:2.0.3")

    //wheel
    implementation ("com.github.commandiron:SpinWheelCompose:1.1.1")

    //billing
    implementation ("com.android.billingclient:billing-ktx:6.0.1")

    //integrity
    implementation ("com.google.android.play:integrity:1.2.0")

    //NTP
    implementation ("commons-net:commons-net:3.8.0")


}