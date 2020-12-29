package com.shoprunner.baleen.poet

import com.squareup.kotlinpoet.FileSpec
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File
import java.io.StringWriter
import java.net.URLClassLoader

internal fun assertThat(actual: FileSpec) = FileSpecAssert(actual)

internal class FileSpecAssert(actual: FileSpec) : AbstractAssert<FileSpecAssert, FileSpec>(actual, FileSpecAssert::class.java) {

    fun isEqualTo(expected: String): FileSpecAssert {
        val writer = StringWriter()
        actual.writeTo(writer)
        val output = writer.toString()

        Assertions.assertThat(output).isEqualTo(expected)

        return this
    }

    fun isEqualToIgnoringWhitespace(expected: String): FileSpecAssert {
        val writer = StringWriter()
        actual.writeTo(writer)
        val output = writer.toString()

        Assertions.assertThat(output).isEqualToIgnoringWhitespace(expected)

        return this
    }

    fun canCompile(): FileSpecAssert {

        val dir = File("build/baleen-poet-test")
        val sourceDir = File(dir, "src/main/kotlin")
        sourceDir.mkdirs()
        val classesDir = File(dir, "classes/main/kotlin")
        classesDir.mkdirs()

        val packageDir = actual.packageName
            .takeUnless { it.isBlank() }
            ?.let { File(sourceDir, it.replace(".", "/")) }
            ?: sourceDir

        actual.writeTo(sourceDir)

        val baleenFile = File(packageDir, "${actual.name.toCleanFileName()}.kt")
        Assertions.assertThat(baleenFile).exists()

        if (System.getenv("GEN_CLASSPATH")?.isNotBlank() == true) {
            val compiler = K2JVMCompiler()
            val args = K2JVMCompilerArguments().apply {
                destination = classesDir.path
                freeArgs = listOf(baleenFile.path)
                classpath = System.getenv("GEN_CLASSPATH")
                noStdlib = true
            }
            compiler.exec(LogMessageCollector, Services.EMPTY, args)

            val classesPackageDir = actual.packageName
                .takeUnless { it.isBlank() }
                ?.let { File(classesDir, it.replace(".", "/")) }
                ?: classesDir

            val compiledClassName = actual.name.toCleanFileName()

            // Check that compilation worked
            Assertions.assertThat(
                File(
                    classesPackageDir,
                    "${compiledClassName}Kt.class"
                )
            ).exists()

            // Check if the compiled files can be loaded
            val cl = URLClassLoader(arrayOf(classesDir.toURI().toURL()))

            val fullClassName = if (actual.packageName.isBlank()) {
                "${compiledClassName}Kt"
            } else {
                "${actual.packageName}.${compiledClassName}Kt"
            }

            Assertions.assertThat(cl.loadClass(fullClassName)).isNotNull
        }

        return this
    }
}
