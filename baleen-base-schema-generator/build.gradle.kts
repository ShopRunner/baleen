plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}
