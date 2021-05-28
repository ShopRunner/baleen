package com.shoprunner.baleen.kapt

import javax.annotation.processing.Messager

fun Messager.error(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.ERROR, "${message()}\r\n")
}

fun Messager.note(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.NOTE, "${message()}\r\n")
}

fun Messager.warning(message: () -> String) {
    this.printMessage(javax.tools.Diagnostic.Kind.WARNING, "${message()}\r\n")
}
