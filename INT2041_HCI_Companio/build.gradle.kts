
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

val composeVersion: String by project
val cameraxVersion: String by project


buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    }
}

extra["compose_version"] = "1.1.1"
extra["camerax_version"] = "1.2.0-alpha03"

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}

