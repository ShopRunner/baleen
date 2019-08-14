package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UnionTypeTest {

    @Test
    fun `passes a any union checks pass`() {
        assertThat(UnionType(LongType(), IntType()).validate(dataTrace(), 1)).isEmpty()
        assertThat(UnionType(LongType(), IntType()).validate(dataTrace(), 1L)).isEmpty()
    }

    @Test
    fun `checks none union checks match`() {
        assertThat(UnionType(LongType(), IntType()).validate(dataTrace(), "a string")).containsExactly(
                ValidationError(dataTrace(), "is not a long", "a string"),
                ValidationError(dataTrace(), "is not an Int", "a string"))
    }
}
