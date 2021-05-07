plugins {
    id("com.android.library")

    kotlin("android")
    kotlin("plugin.serialization")
    `maven-publish`
}

group = "net.daverix.kontent"
version = "0.1-SNAPSHOT"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.2")

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("io.strikt:strikt-core:0.30.0")
}

val androidSourceJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("library") {
                from(components["release"])
                artifactId = "kontent-serialization"

                artifact(androidSourceJar) {
                    classifier = "sources"
                }
            }
        }
    }
}