plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.20")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
    implementation("org.jmailen.gradle:kotlinter-gradle:3.3.0")
    implementation("de.marcphilipp.gradle:nexus-publish-plugin:0.4.0")
}
