import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.2.51"
}

group = "se"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-gradle-serialization-plugin:0.6.0")
    compile(kotlin("stdlib-jdk8"))
    implementation("com.beust:klaxon:3.0.1")
    testCompile("junit", "junit", "4.12")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.6.0")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}