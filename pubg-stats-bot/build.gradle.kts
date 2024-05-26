group = "dev.pubgstats.bot"

dependencies {
    api(libs.slf4j)
    api(libs.pubgkt)
    api(libs.coroutines.core)

    testImplementation(libs.coroutines.test)
}
