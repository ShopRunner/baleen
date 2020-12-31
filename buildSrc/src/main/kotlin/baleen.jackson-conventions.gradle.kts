plugins {
    id("kotlin")
}

dependencies {
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.12.0"))
    api("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
