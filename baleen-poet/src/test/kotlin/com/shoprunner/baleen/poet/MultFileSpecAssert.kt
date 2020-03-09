package com.shoprunner.baleen.poet

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.io.StringWriter
import java.net.URLClassLoader
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services

internal fun assertThat(actual: List<FileSpec>) = MultFileSpecAssert(actual)

internal class MultFileSpecAssert(actual: List<FileSpec>) : AbstractAssert<MultFileSpecAssert, List<FileSpec>>(actual, MultFileSpecAssert::class.java) {

    fun isEqualTo(expected: List<String>): MultFileSpecAssert {
        val output = actual.map {
            val writer = StringWriter()
            it.writeTo(writer)
            writer.toString()
        }

        Assertions.assertThat(output.zip(expected)).allSatisfy { (a, e) ->
            Assertions.assertThat(a).isEqualTo(e)
        }

        return this
    }

    fun isEqualToIgnoringWhitespace(expected: List<String>): MultFileSpecAssert {
        val output = actual.map {
            val writer = StringWriter()
            it.writeTo(writer)
            writer.toString()
        }

        Assertions.assertThat(output.zip(expected)).allSatisfy { (a, e) ->
            Assertions.assertThat(a).isEqualToIgnoringWhitespace(e)
        }

        return this
    }

    fun canCompile(): MultFileSpecAssert {

        val dir = File("build/baleen-poet-test")
        val sourceDir = File(dir, "src/main/kotlin")
        sourceDir.mkdirs()
        val classesDir = File(dir, "classes/main/kotlin")
        classesDir.mkdirs()

        actual.forEach { it.writeTo(sourceDir) }

        Assertions.assertThat(actual).allSatisfy {
            val packageDir = it.packageDir(sourceDir)
            val baleenFile = File(packageDir, "${it.name.toCleanFileName()}.kt")
            Assertions.assertThat(baleenFile).exists()
        }

        if (System.getenv("GEN_CLASSPATH")?.isNotBlank() == true) {
            val compiler = K2JVMCompiler()
            val args = K2JVMCompilerArguments().apply {
                destination = classesDir.path
                freeArgs = actual.map { File(it.packageDir(sourceDir), "${it.name.toCleanFileName()}.kt").path }
                classpath = System.getenv("GEN_CLASSPATH")
                noStdlib = true
            }
            compiler.exec(LogMessageCollector, Services.EMPTY, args)

            Assertions.assertThat(actual).allSatisfy {
                val classesPackageDir = it.packageDir(classesDir)
                val compiledClassName = it.name.toCleanFileName()

                // Check that compilation worked
                Assertions.assertThat(
                    File(
                        classesPackageDir,
                        "${compiledClassName}Kt.class"
                    )
                ).exists()

                // Check if the compiled files can be loaded
                val cl = URLClassLoader(arrayOf(classesDir.toURI().toURL()))

                val fullClassName = if (it.packageName.isBlank()) {
                    "${compiledClassName}Kt"
                } else {
                    "${it.packageName}.${compiledClassName}Kt"
                }

                Assertions.assertThat(cl.loadClass(fullClassName)).isNotNull
            }
        }

        return this
    }

    private fun FileSpec.packageDir(sourceDir: File): File = this.packageName
        .takeUnless { it.isBlank() }?.let { File(sourceDir, it.replace(".", "/")) } ?: sourceDir
}
