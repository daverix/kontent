include(":serialization")

rootProject.name = "kontent"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application",
                "com.android.library" -> {
                    useModule("com.android.tools.build:gradle:4.2.0")
                }
            }
        }
    }
    plugins {
        kotlin("android") version "1.5.0" apply false
        kotlin("plugin.serialization") version "1.5.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        // org.jetbrains.trove4j:trove4j:20160824.
        gradlePluginPortal()
    }
}