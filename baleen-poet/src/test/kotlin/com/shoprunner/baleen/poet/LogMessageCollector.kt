package com.shoprunner.baleen.poet

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import java.util.logging.Logger

internal object LogMessageCollector : MessageCollector {
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
