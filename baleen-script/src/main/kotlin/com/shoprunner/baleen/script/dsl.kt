package com.shoprunner.baleen.script

import com.shoprunner.baleen.*
import org.jetbrains.kotlin.konan.file.use

fun environmentCredentials(): Credentials {
    val dataSourceProperties = System.getenv()
        .filterKeys { it.startsWith("DATABASE_") && it !in listOf("DATABASE_URL", "DATABASE_USER", "DATABASE_PASSWORD") }
        .map { (k, v) -> k.replace("DATABASE_", "") to v }
        .toMap()

    return Credentials(
        url = System.getenv("DATABASE_URL"),
        user = System.getenv("DATABASE_USER"),
        password = System.getenv("DATABASE_PASSWORD"),
        dataSourceProperties = dataSourceProperties
    )
}

fun database(
    credentials: Credentials = environmentCredentials(),
    body: DatabaseValidationWorker.() -> Unit
): List<ValidationResult> {
    val worker = DatabaseValidationWorker(credentials)
    worker.use(body)
    return worker.results
}

