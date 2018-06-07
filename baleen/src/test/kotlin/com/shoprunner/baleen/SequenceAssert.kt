package com.shoprunner.baleen

import org.assertj.core.api.AbstractAssert

class SequenceAssert<T>(actual: Sequence<T>) : AbstractAssert<SequenceAssert<T>, Sequence<T>>(actual, SequenceAssert::class.java) {

    companion object {
        fun <T> assertThat(actual: Sequence<T>) = SequenceAssert(actual)
    }

    fun isEmpty(): SequenceAssert<T> {
        if (actual.toList().isNotEmpty()) {
            failWithMessage("sequence <%s> is not empty", actual)
        }
        return this
    }

    fun containsExactly(vararg match: T): SequenceAssert<T> {
        if (!actual.zip(match.asSequence()).all { (a, e) -> a == e }) {
            failWithMessage("sequence <%s> is not the same as", match)
        }
        return this
    }
}