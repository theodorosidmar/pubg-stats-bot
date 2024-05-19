import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    }

    tasks {
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JVM_21)
            }
        }
    }
}
