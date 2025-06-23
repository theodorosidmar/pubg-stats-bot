plugins {
    alias(libs.plugins.kotlin.dsl) apply true
    alias(libs.plugins.kotlin.jvm) apply false
}

kotlin {
    jvmToolchain(24)
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    }

    dependencies {
        testImplementation(rootProject.libs.kotlin.test)
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}
