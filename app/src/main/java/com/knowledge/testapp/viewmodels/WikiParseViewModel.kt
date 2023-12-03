package com.knowledge.testapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowledge.testapp.utils.ModifyingStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class WikiParseViewModel: ViewModel() {
    private val _options = MutableLiveData<List<String>>()
    val options: LiveData<List<String>> = _options
    private val okHttpClient = OkHttpClient()

    fun fetchTitles(articleUrl: String) {
        viewModelScope.launch {
            fetchTitlesFromWikipediaSections(
                articleUrl = articleUrl,
                maxTitles = 200,
                onTitlesFetched = { titles ->
                    _options.value = titles
                }
            )
        }
    }

    private suspend fun fetchTitlesFromWikipediaSections(
        articleUrl: String,
        maxTitles: Int = 200,
        onTitlesFetched: (List<String>) -> Unit
    ) {
        var section = 0
        val titlesSet = linkedSetOf<String>()
        var reachedMaxTitles = false

        while (titlesSet.size < maxTitles) {
            val sectionUrl = "$articleUrl&section=$section"

            val responseString = withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(sectionUrl).build()
                    okHttpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.string()
                        } else null
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }

            var foundNewTitles = false
            try {
                responseString?.let {
                    var trimmedText = (it)
                    if(section == 0) {
                        trimmedText = extractTextAfterFirstBoldParagraph(it)
                    }
                    val decodedText = ModifyingStrings.decodeHtmlEntities(trimmedText)

                    val linkRegex = Regex("/wiki/([^\\\\]+)\\\\")
                    val unwantedTitlePattern = Regex("ISO \\d+-\\d+")
                    val links = linkRegex.findAll(decodedText).map { match ->
                        ModifyingStrings.encodedURLToText(match.groupValues[1])
                    }.filter { title ->
                        !title.endsWith("(identifier)") && !title.contains(":") && !unwantedTitlePattern.matches(title)
                    }.toList()

                    links.forEach { title ->
                        val added = titlesSet.add(title)
                        if (added) {
                            foundNewTitles = true
                        }
                        if (titlesSet.size >= maxTitles) {
                            reachedMaxTitles = true
                            return@forEach  // Skip further processing in forEach
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return
                // Handle exceptions
            }

            onTitlesFetched(titlesSet.toList()) // Convert set to list and callback
            section++

            if (!foundNewTitles || reachedMaxTitles) break
        }
    }

    suspend fun isTitleCorrect(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val result = response.body?.string()
                        result != null && !result.contains("Wikipedia does not have an article with this exact name")
                    } else false
                }
            } catch (e: IOException) {
                false
            }
        }
    }

    fun cutToThreeSentencesOrSixtyWords(text: String): String {
        var sentenceCount = 0
        var wordCount = 0

        val stringBuilder = StringBuilder()
        text.split(" ").forEach { word ->
            stringBuilder.append("$word ")
            wordCount++

            if (word.endsWith('.') || word.endsWith('?') || word.endsWith('!')) {
                sentenceCount++
                if (wordCount >= 60) {
                    return stringBuilder.trim().toString()
                }
            }
        }

        return stringBuilder.trim().toString()
    }

    fun extractTextAfterFirstBoldParagraph(response: String): String {
        // Extract the article title
        val title = extractRawTitle(response)

        // Split the title into words
        val titleWords = title.split(" ")

        // Find the first occurrence of "<p><b>" that contains at least one word from the title
        var startIndex = -1
        for (word in titleWords) {
            val pattern = "<p><b>$word"
            startIndex = response.indexOf(pattern)
            if (startIndex != -1) {
                break
            }
        }

        return if (startIndex != -1) {
            // Keep everything after the found pattern
            response.substring(startIndex)
        } else {
            // If a matching pattern is not found, return a default message
            response
        }
    }

    fun extractRawTitle(response: String): String {
        val titlePrefix = "\"title\":\""
        val startIndex = response.indexOf(titlePrefix)
        if (startIndex != -1) {
            val endIndex = response.indexOf("\"", startIndex + titlePrefix.length)
            if (endIndex != -1) {
                return response.substring(startIndex + titlePrefix.length, endIndex)
            }
        }
        return "Title not found"
    }

    suspend fun fetchFirstSectionText(articleUrl: String): String {
        val sectionUrl = "$articleUrl&section=0"  // Fetching the first section

        // Fetch response from the API
        val responseString = withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(sectionUrl).build()
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()
                    } else null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        // Process the response string
        return responseString?.let {
            var trimmedText = extractTextAfterFirstBoldParagraph(it)
            // You need to determine how to find the end of the first section
            // This is an example, adjust according to the actual structure
            val firstSection = trimmedText.substringBefore("<h2>") // or another appropriate delimiter
            val cleanedDescription = cleanHtmlContent(firstSection)
            var description = ModifyingStrings.decodeUnicodeEscapes(cleanedDescription)
            description = cutToThreeSentencesOrSixtyWords(description)
            System.out.println(description)

            description
        } ?: "No content found"  // Default value if responseString is null
    }

    fun cleanHtmlContent(htmlContent: String): String {
        // Remove any <span> tag with "error" inside it and everything after it
        val errorPattern = Regex("<span [^>]*error[^>]*>.*")
        val cleanedHtml = htmlContent.replace(errorPattern, "")

        // Remove HTML tags
        var text = cleanedHtml.replace(Regex("<[^>]*>"), " ")

        // Replace common HTML entities
        text = text.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&#91;", "[")  // '[' character
            .replace("&#93;", "]")  // ']' character

        // Remove reference markers like [1]
        text = text.replace(Regex("\\[\\d+\\]"), "")

        // Remove newline characters
        text = text.replace("\\n", " ") // Using "\\n" to handle the escape character

        // Optional: Replace multiple consecutive spaces with a single space
        text = text.replace(Regex(" +"), " ")

        return text.trim()
    }
}
