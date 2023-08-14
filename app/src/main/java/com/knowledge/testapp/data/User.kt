package com.knowledge.testapp.data

data class User(
    var username: String = "",
    var email: String = "",
    var language: Language = Language.ENGLISH,
    var recordsHeld: Int = 0,
    var currentScore: Int = 0
) {
    val languageCode: String
        get() {
            return when (language) {
                Language.ENGLISH -> "EN"
                Language.POLISH -> "PL"
                // Add more cases for other languages if needed
            }
        }
}