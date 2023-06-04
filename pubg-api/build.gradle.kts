import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_19
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(module = "jvm") version "1.8.21"
    kotlin(module = "plugin.serialization") version "1.8.21"
}

group = "com.github.theodorosidmar.pubg"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor (client)
    val ktorVersion = "2.3.0"
    implementation(group = "io.ktor", name = "ktor-client-core", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-cio", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-logging", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-content-negotiation", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-serialization-kotlinx-json", version = ktorVersion)
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JVM_19)
        }
    }
}
