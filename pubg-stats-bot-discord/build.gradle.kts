group = "dev.pubgstats.bot.discord"

dependencies {
    // Stats bot
    implementation(libs.pubgkt)

    // Kord
    implementation(libs.kord)

    // Logging
    runtimeOnly(libs.logback.classic)
}

tasks {
    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "dev.pubgstats.bot.discord"
        }
        val dependencies = configurations
            .runtimeClasspath
            .get()
            .map {
                if (it.isDirectory) it
                else zipTree(it)
            }
        from(dependencies)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
