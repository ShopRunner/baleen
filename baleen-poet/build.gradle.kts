plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    api("com.squareup:kotlinpoet:1.11.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler")
}

val compileTestKotlin = tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileTestKotlin")

tasks.test {
    environment("GEN_CLASSPATH",
        (configurations.testCompileClasspath.get().files.map(File::getPath) + files(compileTestKotlin.destinationDir))
            .joinToString(":")
    )
}
