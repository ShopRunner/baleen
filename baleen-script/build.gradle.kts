plugins {
    id("baleen.project-conventions")
    id("baleen.jackson-conventions")
    id("baleen.publish")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

dependencies {
    api(project(":baleen"))
    implementation(project(":baleen-csv"))
    implementation(project(":baleen-db:baleen-jdbc"))
    implementation(project(":baleen-xml"))
    api(project(":baleen-json-jackson"))
    api(project(":baleen-printer:baleen-printer-csv"))
    api(project(":baleen-printer:baleen-printer-html"))
    api(project(":baleen-printer:baleen-printer-java-logger"))
    api(project(":baleen-printer:baleen-printer-text"))
    // for baleen-csv
    implementation("com.opencsv:opencsv:5.5.2")

    testImplementation("org.mock-server:mockserver-netty:5.11.2")
    testImplementation("org.mock-server:mockserver-junit-jupiter:5.11.2")
    testRuntimeOnly("com.h2database:h2:2.1.210")
}


//// Shadow ALL dependencies:
//tasks.create<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
//    target = tasks["shadowJar"] as com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
//    prefix = ""
//}

// Configure Shadow to output with normal jar file name:
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").configure {
//    dependsOn(tasks["relocateShadowJar"])
    minimize()
    archiveClassifier.set("")
}

// Disabling default jar task as jar is output by shadowJar
tasks.named("jar").configure {
    enabled = false
    dependsOn(tasks["shadowJar"])
}

// Disable Gradle module.json as it lists wrong dependencies
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

// Remove dependencies from POM: uber jar has no dependencies
configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            if (name == "mavenKotlin") {
                artifact(tasks["shadowJar"])

                pom.withXml {
                    val pomNode = asNode()

                    val dependencyNodes: groovy.util.NodeList = pomNode.get("dependencies") as groovy.util.NodeList
                    dependencyNodes.forEach {
                        (it as groovy.util.Node).parent().remove(it)
                    }
                }
            }
        }
    }
}
