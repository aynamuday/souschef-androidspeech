import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
//    id("com.google.protobuf") version "0.9.1"
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
        kotlinCompilerExtensionVersion = "1.5.1"    //1.5.1
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

    implementation("androidx.compose.material:material:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("io.coil-kt.coil3:coil-compose:3.0.2")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.2")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore")

//    implementation("com.algolia:algoliasearch-client-kotlin:3.12.1")
//    implementation("io.ktor:ktor-client-android:3.0.3")
    implementation ("com.algolia:instantsearch-compose:3.3.1")
    implementation ("com.algolia:instantsearch-android-paging3:3.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation ("androidx.compose.material:material-icons-extended:1.7.6")


    implementation (platform("com.google.cloud:libraries-bom:26.51.0"))
//    implementation("com.google.auth:google-auth-library-oauth2-http")
//    implementation("com.google.api-client:google-api-client")
//    implementation("com.google.cloud:google-cloud-speech")
//    implementation("com.google.cloud:google-cloud-texttospeech")


//    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.42.0")
}

//configurations.all {
        // i can exclude javalite and proto-google-common-protos
////    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
////    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
////    exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")
////    exclude(group = "com.google.protobuf", module = "protobuf-java")
//
//    resolutionStrategy {
//        force("com.google.protobuf:protobuf-java:3.0.2")
//    }
//}




