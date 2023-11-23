package com.knowledge.testapp.data

data class User(
    var username: String = "",
    var email: String = "",
    var language: Language = Language.ENGLISH,
    var recordsHeld: Int = 0,
    var currentScore: Int = 0
)