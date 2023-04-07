plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    implementation(project(":baleen"))
    implementation(project(":baleen-kotlin:baleen-kotlin-api"))
    implementation("com.squareup:kotlinpoet:1.13.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler")
}

tasks.test {
    environment("GEN_CLASSPATH",
        configurations.testCompileClasspath.get().files.joinToString(":", transform = File::getPath)
    )
}
