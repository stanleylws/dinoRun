import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.stanl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${project.property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${project.property("gdxVersion")}")
    implementation("com.badlogicgames.gdx:gdx-platform:${project.property("gdxVersion")}:natives-desktop")
    implementation("com.badlogicgames.ashley:ashley:1.7.3")
    implementation("io.github.libktx:ktx-app:${project.property("ktxVersion")}")
    implementation("io.github.libktx:ktx-log:${project.property("ktxVersion")}")
    implementation("io.github.libktx:ktx-graphics:${project.property("ktxVersion")}")
    implementation("io.github.libktx:ktx-ashley:${project.property("ktxVersion")}")
    implementation("io.github.libktx:ktx-collections:${project.property("ktxVersion")}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}
