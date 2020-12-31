plugins {
    id("baleen.project-conventions")
    id("kotlin-kapt")
    id("baleen.publish")
}

dependencies {
    implementation(project(":baleen-kotlin:baleen-kotlin-api"))
    implementation("com.squareup:kotlinpoet:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata:1.7.2")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.7.2")
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    kapt("com.google.auto.service:auto-service:1.0-rc6")

    kaptTest(project(":baleen-kotlin:baleen-kotlin-kapt"))
}
