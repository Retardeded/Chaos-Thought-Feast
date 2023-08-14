package com.knowledge.testapp.data

data class User(
    var username: String = "",
    var email: String = "",
    var language: Language = Language.ENG,
    var recordsHeld: Int = 0,
    var currentScore: Int = 0
)