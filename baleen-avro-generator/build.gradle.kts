plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    api("org.apache.avro:avro:1.10.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.squareup:kotlinpoet:1.14.2")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler")
}

tasks.test {
    environment("GEN_CLASSPATH",
        configurations.testCompileClasspath.get().files.joinToString(":", transform = File::getPath)
    )
}
