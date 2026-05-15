import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.FailOnSeverity.Warning

plugins {
    alias(libs.plugins.kotlin.jvm) apply true
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.kotlinx.kover) apply true
}

dependencies {
    detektPlugins(libs.detekt.ktlint)

    // Kord
    implementation(libs.kord)

    // pubgkt
    implementation(platform(libs.pubgkt.bom))
    implementation(libs.pubgkt.core)

    // Logs
    implementation(libs.slf4j.simple)

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    jvmToolchain(26)
}

detekt {
    config.setFrom(rootProject.file("detekt/detekt.yml"))
    buildUponDefaultConfig = false
    autoCorrect = true
    parallel = true
    debug = false
    ignoreFailures = false
    failOnSeverity = Warning
}

tasks {
    check {
        dependsOn(detektMain, detektTest)
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "dev.pubgstats.bot.discord.MainKt"
        }
        val dependencies =
            configurations
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

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Detekt>().configureEach {
        jvmTarget = "25"
        reports {
            html.required.set(true)
            markdown.required.set(true)
            sarif.required.set(true)
            checkstyle.required.set(false)
        }
    }
}
