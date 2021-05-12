plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    testRuntimeOnly("com.h2database:h2:1.4.200")
}
