plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    implementation(project(":baleen-csv"))
    implementation(project(":baleen-db:baleen-jdbc"))
    implementation(project(":baleen-json-jackson"))
    implementation(project(":baleen-xml"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("script-util"))
    implementation(kotlin("compiler-embeddable"))
    runtimeOnly(kotlin("scripting-compiler-embeddable"))
    testRuntimeOnly(kotlin("scripting-compiler-embeddable"))
}
