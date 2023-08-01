@file:Suppress("PropertyName", "SpellCheckingInspection")

import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
}

repositories {
    mavenCentral()
}


tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    withType<Test>().configureEach {
        useJUnitPlatform {

        }
    }
}



val junit_version = "5.9.2"
val kotest_version = "5.6.2"
val mockk_version = "1.13.4"
val kotlinx_serialization_json_version = "1.5.0"
val kotlinx_serialization_kyaml_version = "0.55.0"
val jnanoid_version = "2.0.0"
val clikt_version = "4.1.0"
val mordant_version = "2.1.0"
val json5k_version = "0.3.0"

dependencies {

    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-framework-datatest:$kotest_version")
    testImplementation("io.mockk:mockk:$mockk_version")

    constraints {
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json_version")
        api("com.charleskorn.kaml:kaml:$kotlinx_serialization_kyaml_version")
        api("com.aventrix.jnanoid:jnanoid:$jnanoid_version")
        api("com.github.ajalt.clikt:clikt:$clikt_version")
        api("com.github.ajalt.mordant:mordant:$mordant_version")
        api("io.github.xn32:json5k:$json5k_version")
        api("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.5.1")
    }
}

