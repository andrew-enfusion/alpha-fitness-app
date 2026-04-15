import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.legacy.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.andrewenfusion.alphafitness"
    compileSdk = 36

    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }
    val openAiApiKey = resolveConfigValue(
        localProperties = localProperties,
        propertyName = "OPENAI_API_KEY",
        defaultValue = "",
    )
    val openAiModel = resolveConfigValue(
        localProperties = localProperties,
        propertyName = "OPENAI_MODEL",
        defaultValue = "gpt-4o-mini",
    )
    val openAiResponsesUrl = resolveConfigValue(
        localProperties = localProperties,
        propertyName = "OPENAI_RESPONSES_URL",
        defaultValue = "https://api.openai.com/v1/responses",
    )
    val onboardingAiTimeoutSeconds = resolveConfigValue(
        localProperties = localProperties,
        propertyName = "OPENAI_TIMEOUT_SECONDS",
        defaultValue = "30",
    )

    defaultConfig {
        applicationId = "com.andrewenfusion.alphafitness"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "OPENAI_API_KEY", quoteBuildConfig(openAiApiKey))
        buildConfigField("String", "OPENAI_MODEL", quoteBuildConfig(openAiModel))
        buildConfigField("String", "OPENAI_RESPONSES_URL", quoteBuildConfig(openAiResponsesUrl))
        buildConfigField("int", "OPENAI_TIMEOUT_SECONDS", onboardingAiTimeoutSeconds)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)

    kapt(libs.androidx.room.compiler)
    kapt(libs.hilt.compiler)

    testImplementation(libs.junit)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

fun resolveConfigValue(
    localProperties: Properties,
    propertyName: String,
    defaultValue: String,
): String =
    providers.gradleProperty(propertyName).orNull
        ?: System.getenv(propertyName)
        ?: localProperties.getProperty(propertyName)
        ?: defaultValue

fun quoteBuildConfig(value: String): String =
    "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"") + "\""
