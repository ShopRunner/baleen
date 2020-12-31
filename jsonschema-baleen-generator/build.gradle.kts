plugins {
    id("baleen.project-conventions")
    id("baleen.jackson-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":jsonschema-model"))
    implementation(project(":baleen"))
    implementation("com.squareup:kotlinpoet:1.7.2")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler")
}

tasks.test {
    environment("GEN_CLASSPATH",
            configurations.testCompileClasspath.get().files.joinToString(":", transform = File::getPath)
    )
}
