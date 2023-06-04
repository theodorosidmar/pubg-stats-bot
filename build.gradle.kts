import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_20
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.theodorosidmar.pubgstats"

plugins {
    kotlin(module = "jvm") version "1.9.0-Beta"
}

repositories {
    mavenCentral()
}

dependencies {
    // Kord
    implementation(group = "dev.kord", name = "kord-core", version = "0.9.0")

    // Logging
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.7")
    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.4.7")

    // PUBG API
    implementation(group = "com.github.theodorosidmar.pubg", name = "pubg-api", version = "0.0.1")
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JVM_20)
        }
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "com.github.theodorosidmar.pubgstats.MainKt"
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
