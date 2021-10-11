plugins {
    id("baleen.project-conventions")
}

dependencies {
    api(project(":baleen"))
    implementation(project(":baleen-base-schema-generator"))
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.0")
    runtimeOnly("com.sun.xml.bind:jaxb-impl:3.0.2")
}
