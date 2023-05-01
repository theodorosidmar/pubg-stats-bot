import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_19
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.theodorosidmar.pubg"

plugins {
    kotlin(module = "jvm") version "1.8.21"
    kotlin(module = "plugin.serialization") version "1.8.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "dev.kord", name = "kord-core", version = "0.9.0")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.7")
    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.4.7")

    implementation(group = "io.ktor", name = "ktor-client-core", version = "2.3.0")
    implementation(group = "io.ktor", name = "ktor-client-cio", version = "2.3.0")
    implementation(group = "io.ktor", name = "ktor-client-logging", version = "2.3.0")
    implementation(group = "io.ktor", name = "ktor-serialization-kotlinx-json", version = "2.3.0")
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JVM_19)
        }
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "com.github.theodorosidmar.pubg.MainKt"
        }
        val dependencies = configurations
            .runtimeClasspath
            .get()
            .map {
                if (it.isDirectory) {
                    it
                } else {
                    zipTree(it)
                }
            }
        from(dependencies)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
