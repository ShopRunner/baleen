plugins {
    id("baleen.project-conventions")
    id("kotlin-kapt")
}

dependencies {
    implementation(project(":baleen-kotlin:baleen-kotlin-api"))
    kapt(project(":baleen-kotlin:baleen-kotlin-kapt"))
}
