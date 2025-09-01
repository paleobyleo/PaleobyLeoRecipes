plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt") // For Room annotation processor
}

android {
    namespace = "com.leo.paleorecipes"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.leo.paleorecipes"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("paleo-release-key.jks")
            storePassword = "beesten01" // Replace with your actual password
            keyAlias = "paleokey"
            keyPassword = "beesten01" // Replace with your actual password
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        compose = true // Enable Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Use appropriate version
    }
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Room database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0") // Room annotation processor

    // Gson for JSON serialization/deserialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Compose dependencies
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    // AndroidX Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // AndroidX SplashScreen library
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
}

// Task to export the release APK to a specific location
tasks.register("exportRelease") {
    dependsOn("assembleRelease")
    doLast {
        val outputDir = File(System.getProperty("user.home") + "/Downloads/PaleoRecipes")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val sourceApk = File("${project.buildDir}/outputs/apk/release/app-release.apk")
        val destApk = File(outputDir, "PaleoRecipes.apk")

        if (sourceApk.exists()) {
            sourceApk.copyTo(destApk, overwrite = true)
            println("APK exported to: ${destApk.absolutePath}")
        } else {
            throw GradleException("Release APK not found at ${sourceApk.absolutePath}")
        }
    }
}

// Task to install the release APK to a connected device
tasks.register("installReleaseToDevice") {
    dependsOn("assembleRelease")
    doLast {
        val apkDir = File("${project.buildDir}/outputs/apk/release")
        val apkFiles = apkDir.listFiles { file -> file.name.endsWith(".apk") }

        if (apkFiles != null && apkFiles.isNotEmpty()) {
            val apkPath = apkFiles[0].absolutePath
            println("Installing APK: $apkPath")
            exec {
                commandLine("adb", "install", "-r", apkPath)
            }
            println("APK installed on connected device")
        } else {
            throw GradleException("No APK files found in ${apkDir.absolutePath}")
        }
    }
}