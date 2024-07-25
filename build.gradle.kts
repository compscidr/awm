plugins {
    alias(libs.plugins.allopen) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    //alias(libs.plugins.quarkus) apply false // this causes "Could not initialize class com.android.sdklib.repository.AndroidSdkHandler"
    alias(libs.plugins.kotlinx.serialization) apply false
}