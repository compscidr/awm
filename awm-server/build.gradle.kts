plugins {
    id("application")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.quarkus)
    alias(libs.plugins.allopen)
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

dependencies {
    implementation(project(":awm-common"))

    api(libs.slf4j.api)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.serialization.json)

    val quarkusBom = enforcedPlatform(libs.quarkus.bom)
    implementation(quarkusBom)
    implementation(libs.bundles.quarkus)
}