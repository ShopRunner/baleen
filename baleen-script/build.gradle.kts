plugins {
    id("baleen.project-conventions")
    id("baleen.jackson-conventions")
    id("baleen.publish")
    application
}

dependencies {
    api(project(":baleen"))
    implementation(project(":baleen-csv"))
    implementation(project(":baleen-db:baleen-jdbc"))
    implementation(project(":baleen-json-jackson"))
    implementation(project(":baleen-xml"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("compiler-embeddable"))
    runtimeOnly(kotlin("scripting-compiler-embeddable"))
    testRuntimeOnly(kotlin("scripting-compiler-embeddable"))
    testRuntimeOnly("com.h2database:h2:1.4.200")
}

application {
    mainClass.set("com.shoprunner.baleen.script.Main")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

val run by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}