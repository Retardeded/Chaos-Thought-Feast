package com.knowledge.testapp.utils

import java.net.URLDecoder
import java.net.URLEncoder

class ModyfingStrings {
    companion object {
        fun sanitizeEmail(email: String): String {
            return email.replace(".", ",")
        }

        fun textToEncodedURL(displayText: String): String {
            return URLEncoder.encode(displayText, "UTF-8")
        }

        // Function to convert encoded URL to display text
        fun encodedURLToText(encodedURL: String): String {
            return URLDecoder.decode(encodedURL, "UTF-8")
        }
    }
}