import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}


group = "net.adamjak.tomas"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("commons-cli:commons-cli:1.5.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}