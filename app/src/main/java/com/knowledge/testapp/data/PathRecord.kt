package com.knowledge.testapp.data

data class PathRecord(
    var startConcept: String = "",
    var goalConcept: String = "",
    var path: ArrayList<String> = ArrayList(),
    var steps: Int = 0,
    var win: Boolean = false
)