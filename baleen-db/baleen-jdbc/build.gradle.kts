plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    testRuntimeOnly("com.h2database:h2:2.0.202")
}
