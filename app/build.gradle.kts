import java.util.Properties

val developerSigningRequested = providers.gradleProperty("viquickball.developerSigning")
    .orNull
    ?.let { value ->
        when (value.lowercase()) {
            "true" -> true
            "false" -> false
            else -> throw GradleException("viquickball.developerSigning must be true or false.")
        }
    } ?: false

val developerKeystoreProperties = if (developerSigningRequested) {
    val propertiesFile = rootProject.file("keystore.properties")
    if (!propertiesFile.isFile) {
        throw GradleException(
            "Developer signing was requested, but keystore.properties is missing from the project root."
        )
    }
    Properties().apply {
        propertiesFile.inputStream().use { load(it) }
    }
} else {
    null
}

fun requiredSigningProperty(name: String): String =
    developerKeystoreProperties?.getProperty(name)
        ?.takeIf { it.isNotBlank() }
        ?: throw GradleException("Developer signing was requested, but '$name' is missing from keystore.properties.")

val developerKeystoreFile = if (developerSigningRequested) {
    project.file(requiredSigningProperty("storeFile")).also { keystore ->
        if (!keystore.isFile) {
            throw GradleException("Developer signing was requested, but the configured keystore file is unavailable.")
        }
    }
} else {
    null
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.viraj.viquickball"
    signingConfigs {
        if (developerSigningRequested) {
            create("developerRelease") {
                storeFile = developerKeystoreFile
                storePassword = requiredSigningProperty("storePassword")
                keyAlias = requiredSigningProperty("keyAlias")
                keyPassword = requiredSigningProperty("keyPassword")
            }
        }
    }
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.viraj.viquickball"
        minSdk = 27
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            if (developerSigningRequested) {
                signingConfig = signingConfigs.getByName("developerRelease")
            }
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
