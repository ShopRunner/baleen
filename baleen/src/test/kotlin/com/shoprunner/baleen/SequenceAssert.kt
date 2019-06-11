package com.shoprunner.baleen

import org.assertj.core.api.AbstractAssert

class SequenceAssert<T>(actual: Sequence<T>) : AbstractAssert<SequenceAssert<T>, Sequence<T>>(actual, SequenceAssert::class.java) {

    private val actualList = actual.toList()

    companion object {
        fun <T> assertThat(actual: Sequence<T>) = SequenceAssert(actual)
    }

    fun isEmpty(): SequenceAssert<T> {
        if (actualList.isNotEmpty()) {
            failWithMessage("sequence <%s> is not empty", actualList)
        }
        return this
    }

    fun containsExactly(vararg match: T): SequenceAssert<T> {
        val matchList = match.toList()
        if (!actualList.zip(matchList).all { (a, e) -> a == e }) {
            failWithMessage("sequence <%s> is not the same as <%s>", actualList, matchList)
        }
        return this
    }
}
