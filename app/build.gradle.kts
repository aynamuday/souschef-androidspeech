plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
}

android {
    namespace = "com.samsantech.souschef"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.samsantech.souschef"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.client.sdk)
    implementation(libs.androidx.paging.compose.android)     // this

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.compose.material:material:1.7.6")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("io.coil-kt.coil3:coil-compose:3.0.2")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.2")

    implementation("androidx.media:media:1.7.0")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-functions:20.0")
    implementation ("com.sendgrid:sendgrid-java:4.9.3")
    implementation("com.itextpdf:itextg:5.5.10")


//    implementation("com.algolia:algoliasearch-client-kotlin:3.12.1")
//    implementation("io.ktor:ktor-client-android:3.0.3")
//<<<<<<< HEAD
    implementation ("com.algolia:instantsearch-compose:3.3.1")
    implementation ("com.algolia:instantsearch-android-paging3:3.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation ("androidx.compose.material:material-icons-extended:1.7.6")
    implementation ("io.coil-kt:coil-compose:2.1.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")



    implementation (platform("com.google.cloud:libraries-bom:26.51.0"))
//    implementation("com.google.auth:google-auth-library-oauth2-http")
//    implementation("com.google.api-client:google-api-client")
//    implementation("com.google.cloud:google-cloud-speech")
//    implementation("com.google.cloud:google-cloud-texttospeech")


//    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.42.0")
//=======
    implementation("com.algolia:instantsearch-compose:3.3.1")
    implementation("com.algolia:instantsearch-android-paging3:3.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
//>>>>>>> d9afb236b3550ab49f689c9f6237d141d06523ae
}


