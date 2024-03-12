import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.pubgstats"

plugins {
    kotlin(module = "jvm") version "1.9.23"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // Kord
    implementation(group = "dev.kord", name = "kord-core", version = "0.13.1")

    // Logging
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.9")
    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.4.14")

    // PUBG API
    implementation(group = "pubgkt", name = "pubgkt-jvm", version = "0.0.1")
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JVM_21)
        }
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "com.github.theodorosidmar.pubgstats.bot.discord.MainKt"
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
