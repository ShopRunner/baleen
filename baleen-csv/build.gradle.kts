plugins {
    id("baleen.project-conventions")
    id("baleen.publish")
}

dependencies {
    api(project(":baleen"))
    api("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("com.opencsv:opencsv:5.3")
}
