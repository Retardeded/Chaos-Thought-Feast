package com.knowledge.testapp.utils

import com.knowledge.testapp.QuizValues
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

class ModifyingStrings {
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

        // Function to generate article URL
        fun generateArticleUrl(userLanguageCode: String, articleTitle: String): String {
            val basicLinkPrefix = QuizValues.BASIC_LINK_PREFIX.lowercase(Locale.ROOT)
            val basicLinkSuffix = QuizValues.BASIC_LINK_SUFFIX
            return "$basicLinkPrefix$userLanguageCode$basicLinkSuffix${articleTitle.replace(" ", "_")}"
        }

        fun createTableName(
            tableNameSpecification: String,
            lang: String
        ): String {
            return tableNameSpecification + "_" + lang
        }
    }

}