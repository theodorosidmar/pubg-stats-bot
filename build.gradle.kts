plugins {
    alias(libs.plugins.kotlin.jvm) apply true
}

dependencies {
    // Kord
    implementation(libs.kord)

    // pubgkt
    implementation(platform(libs.pubgkt.bom))
    implementation(libs.pubgkt.core)

    // Tests
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(26)
}

tasks {
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
}
