package com.knowledge.testapp.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import org.apache.commons.text.StringEscapeUtils

class ModifyingStrings {
    companion object {
        fun sanitizeEmail(email: String): String {
            return email.replace(".", ",")
        }

        fun decodeHtmlEntities(htmlText: String): String {
            return StringEscapeUtils.unescapeHtml4(htmlText)
        }

        fun decodeUnicodeEscapes(str: String): String {
            val regex = Regex("\\\\u[0-9a-fA-F]{4}")
            return regex.replace(str) {
                it.value.substring(2).toInt(16).toChar().toString()
            }
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
            val basicLinkPrefix = ConstantValues.BASIC_LINK_PREFIX.lowercase(Locale.ROOT)
            val basicLinkInffix = ConstantValues.BASIC_LINK_INFIX
            val basicLinkSuffixEnd = ConstantValues.BASIC_LINK_SUFFIX
            return "$basicLinkPrefix$userLanguageCode$basicLinkInffix${articleTitle.replace(" ", "_")}$basicLinkSuffixEnd"
        }

        fun generateArticleDescriptionUrl(userLanguageCode: String, articleTitle: String): String {
            val basicLinkPrefix = ConstantValues.BASIC_LINK_PREFIX.lowercase(Locale.ROOT)
            val basicLinkInffix = ConstantValues.BASIC_LINK_DESCRIPTION_INFIX
            val basicLinkSuffixEnd = ConstantValues.BASIC_LINK_DESCRIPTION_SUFFIX
            return "$basicLinkPrefix$userLanguageCode$basicLinkInffix${articleTitle.replace(" ", "_")}$basicLinkSuffixEnd"
        }

        fun createTableName(
            tableNameSpecification: String,
            lang: String
        ): String {
            return tableNameSpecification + "_" + lang
        }
    }

}