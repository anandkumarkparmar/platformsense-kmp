plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.binary.compatibility.validator) apply false
}

allprojects {
    group = "io.github.anandkumarkparmar"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.5.0")
        android.set(true)
        outputToConsole.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
        }
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        parallel = true
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "17"
    }

    pluginManager.withPlugin("maven-publish") {
        configure<PublishingExtension> {
            publications.configureEach {
                if (this is MavenPublication) {
                    pom {
                        name.set("PlatformSense KMP")
                        description.set(
                            "Kotlin Multiplatform library for unified platform sensing across Android and iOS.",
                        )
                        url.set("https://github.com/anandkumarkparmar/platformsense-kmp")
                        licenses {
                            license {
                                name.set("The Apache License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                id.set("anandkumarkparmar")
                                name.set("Anand K Parmar")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/anandkumarkparmar/platformsense-kmp.git")
                            developerConnection.set("scm:git:ssh://github.com/anandkumarkparmar/platformsense-kmp.git")
                            url.set("https://github.com/anandkumarkparmar/platformsense-kmp")
                        }
                    }
                }
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
