package com.shoprunner.baleen

object Baleen {
    fun describe(
        name: String,
        nameSpace: String = "",
        markdownDescription: String = "",
        description: (DataDescription) -> Unit
    ): DataDescription {
        val dd = DataDescription(
                name = name,
                nameSpace = nameSpace,
                markdownDescription = markdownDescription)
        description(dd)
        return dd
    }
}
