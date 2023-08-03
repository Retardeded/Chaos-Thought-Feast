package com.knowledge.testapp.data

data class User(
    var username: String = "",
    var email: String = "",
    var recordsHeld: Int = 0,
    var currentScore: Int = 0
)