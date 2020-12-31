plugins {
    id("baleen.project-conventions")
    id("baleen.jackson-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    api(project(":baleen-base-schema-generator"))
    api(project(":jsonschema-model"))
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
