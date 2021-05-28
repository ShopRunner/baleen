plugins {
    id("baleen.project-conventions")
    id("baleen.jackson-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
}
