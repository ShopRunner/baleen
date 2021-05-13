plugins {
    id("org.jetbrains.dokka")
    id("de.marcphilipp.nexus-publish")
    id("org.jetbrains.kotlin.jvm")
    signing
}

val sourceJar = tasks.register<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.getByName("dokkaJavadoc"))
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            pom {
                description.set("Library for Validating Data")
                name.set("Baleen")
                url.set("https://github.com/ShopRunner/baleen")

                organization {
                    name.set("com.shoprunner")
                    url.set("https://github.com/ShopRunner")
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/ShopRunner/baleen/issues")
                }
                licenses {
                    license {
                        name.set("BSD 3-Clause \"New\" or \"Revised\" License")
                        url.set("https://github.com/ShopRunner/baleen/blob/master/LICENSE.txt")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("https://github.com/ShopRunner/baleen")
                    connection.set("scm:git:git://github.com/ShopRunner/baleen.git")
                    developerConnection.set("scm:git:ssh://git@github.com:ShopRunner/baleen.git")
                }
                developers {
                    developer {
                        name.set("Shoprunner")
                    }
                }
            }

            from(components["java"])

            artifact(sourceJar)
            artifact(javadocJar)
        }
    }
}

signing {
    if(!project.hasProperty("skip.signing")) {
        sign(publishing.publications["mavenKotlin"])
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}
