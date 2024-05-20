import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.dsl) apply true
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    }

    dependencies {
        testImplementation(rootProject.libs.kotlin.test)
    }

    tasks {
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JVM_21)
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }
    }
}
