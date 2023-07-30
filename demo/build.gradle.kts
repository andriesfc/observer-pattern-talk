plugins {
    id("common-conventions")
    application
    kotlin("plugin.serialization") version "1.9.0"

}

application {
    mainClass.set("techare.simplevms.demo.DemoAppKt")
    applicationName = "demo-simplevms"
}

dependencies {
    implementation(project(":foundation"))
    implementation("com.aventrix.jnanoid:jnanoid")
    implementation("com.github.ajalt.clikt:clikt")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("com.charleskorn.kaml:kaml")
    implementation("com.github.ajalt.clikt:clikt")
    implementation("com.github.ajalt.mordant:mordant")
}