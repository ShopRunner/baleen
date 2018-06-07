package com.shoprunner.baleen

import com.shoprunner.baleen.datawrappers.HashData

object TestHelper {
    fun <V> dataOf(vararg pair: Pair<String, V>) = HashData(mapOf(*pair))

    fun <V> contextOf(vararg pair: Pair<String, V>) = Context(dataOf(*pair), dataTrace())
}
