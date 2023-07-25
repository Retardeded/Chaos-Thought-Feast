package com.knowledge.testapp.utils

class ModyfingStrings {
    companion object {
        fun sanitizeEmail(email: String): String {
            return email.replace(".", ",")
        }
    }
}