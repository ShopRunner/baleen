@file:JvmName("Main")
package com.shoprunner.baleen.script

import com.shoprunner.baleen.ValidationResult
import java.nio.file.Files
import java.nio.file.Paths
import javax.script.ScriptEngineManager

inline fun <reified T> Any?.castOrError(): T = takeIf { it is T }?.let { it as T }
    ?: throw IllegalArgumentException("Cannot cast $this to expected type ${T::class}")

fun main(args: Array<String>) {
    if(args.isEmpty()) {
        throw IllegalArgumentException("File or dash for stdin required as input: - | file [file+]")
    }

    with(ScriptEngineManager().getEngineByExtension("kts")) {
        eval("""
            import com.shoprunner.baleen.types.*
        """.trimIndent())

        val results = if(args.size == 1 && args[0] == "-") {
            this.eval(System.`in`.reader()).castOrError<List<ValidationResult>>()
        } else {
            args.asList()
                .map { file ->
                    val scriptReader = Files.newBufferedReader(Paths.get(file))
                    this.eval(scriptReader).castOrError<List<ValidationResult>>()
                }
                .flatten()
        }
        results
            .forEach {
                println(it)
            }
    }
}