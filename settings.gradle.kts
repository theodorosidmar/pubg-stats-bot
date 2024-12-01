rootProject.name = "pubg-stats-bot"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/theodorosidmar/pubgkt")
            credentials {
                username = "theodorosidmar"
                password = System.getenv("GITHUB_TOKEN") ?: extra["gpr.key"]?.toString()
            }
        }
    }
}

include(
    "pubg-stats-bot-discord",
)
