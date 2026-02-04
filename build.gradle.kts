import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.shadow)
}

group = "com"
version = "0.0.1"

application {
    mainClass.set("com.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.cio)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.mock)
    implementation(libs.ktor.server.html)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2.database)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
}

tasks.named<ShadowJar>("shadowJar") {
    manifest {
        attributes["Main-Class"] = "com.ApplicationKt"
    }
    archiveBaseName.set("nasa-app")
    archiveClassifier.set("")
    archiveVersion.set("1.0.0")
}