dependencies {
    // Kord
    implementation(libs.kord)
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
}
