
plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hemant.ecofoodtrackerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hemant.ecofoodtrackerapp"
        minSdk = 28
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.android.car.ui:car-ui-lib:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //it is used for make app responsive
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //It is ue for circle image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    //firebase database
    implementation("com.firebaseui:firebase-ui-database:7.1.1")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation("com.google.firebase:firebase-auth:22.1.2")
    
    //swipe refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //lottie files
    implementation("com.airbnb.android:lottie:6.3.0")

    // Import the BoM for the Firebase platform
    implementation("com.google.firebase:firebase-bom:32.7.4")

    // Add the dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")

    //to resolve the error of checkDebugDuplicateClasses
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))

    //picasso library for set image to ImageView
    implementation("com.squareup.picasso:picasso:2.8")

    //get the current location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    //firebase messaging service
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
}