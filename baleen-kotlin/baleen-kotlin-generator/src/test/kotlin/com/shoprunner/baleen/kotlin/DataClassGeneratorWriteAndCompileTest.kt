package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.kotlin.DataClassGenerator.writeDataClassesTo
import com.shoprunner.baleen.types.StringType
import java.io.File
import java.net.URLClassLoader
import java.util.logging.Logger
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.Test

class DataClassGeneratorWriteAndCompileTest {
    inner class LogMessageCollector : MessageCollector {
        val logger = Logger.getLogger("LogMessageCollector")

        override fun clear() = Unit

        override fun hasErrors() = false

        override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
            when (severity) {
                CompilerMessageSeverity.ERROR -> logger.severe("$message : $location")
                CompilerMessageSeverity.EXCEPTION -> logger.severe("$message : $location")
                CompilerMessageSeverity.STRONG_WARNING -> logger.warning("$message : $location")
                CompilerMessageSeverity.WARNING -> logger.warning("$message : $location")
                else -> logger.info("$severity: $message : $location")
            }
        }
    }

    @Test
    fun `generate code from a DataDescription that compiles`() {
        val childModel = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(childModel)
        }

        // Setup
        val dir = File("build/baleen-gen-test")
        val sourceDir = File(dir, "src/main/kotlin")
        sourceDir.mkdirs()
        val classesDir = File(dir, "classes/main/kotlin")
        classesDir.mkdirs()

        // Generate Data Class Files
        parentModel.writeDataClassesTo(sourceDir)

        val childFile = File(sourceDir, "com/shoprunner/baleen/kotlin/test/Child.kt")
        Assertions.assertThat(childFile).exists()

        val parentFile = File(sourceDir, "com/shoprunner/baleen/kotlin/test/Parent.kt")
        Assertions.assertThat(parentFile).exists()

        // Needs the Environment Variable passed in in order to compile. Gradle can give us this.
        val genClassPath = System.getenv("GEN_CLASSPATH")
        if (genClassPath?.isNotBlank() == true) {
            val compiler = K2JVMCompiler()
            val args = K2JVMCompilerArguments().apply {
                destination = classesDir.path
                freeArgs = listOf(sourceDir.path)
                classpath = genClassPath
                noStdlib = true
            }
            compiler.exec(LogMessageCollector(), Services.EMPTY, args)

            // Check that compilation worked
            Assertions.assertThat(File(classesDir, "com/shoprunner/baleen/kotlin/test/Child.class")).exists()
            Assertions.assertThat(File(classesDir, "com/shoprunner/baleen/kotlin/test/Parent.class")).exists()

            // Check if the compiled files can be loaded
            val cl = URLClassLoader(arrayOf(classesDir.toURI().toURL()))

            Assertions.assertThat(cl.loadClass("com.shoprunner.baleen.kotlin.test.Child")).isNotNull()
            Assertions.assertThat(cl.loadClass("com.shoprunner.baleen.kotlin.test.Parent")).isNotNull()
        }
    }
}
