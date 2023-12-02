package com.knowledge.testapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import org.jsoup.nodes.Document

class WikiParseViewModel: ViewModel() {
    private val _options = MutableLiveData<List<String>>()
    val options: LiveData<List<String>> = _options
    private val okHttpClient = OkHttpClient()

    fun fetchTitles(articleUrl: String) {
        viewModelScope.launch {
            fetchTitlesFromWikipediaSections(
                pageTitle = articleUrl,
                maxTitles = 200,
                onTitlesFetched = { titles ->
                    _options.value = titles
                }
            )
        }
    }

    private suspend fun fetchTitlesFromWikipediaSections(
        pageTitle: String,
        maxTitles: Int = 200,
        onTitlesFetched: (List<String>) -> Unit
    ) {
        var section = 0
        val titles = mutableListOf<String>()

        while (titles.size < maxTitles) {
            val apiUrl = "https://en.wikipedia.org/w/api.php?action=parse&page=$pageTitle&prop=links&section=$section&format=json"

            val responseString = withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(apiUrl).build()
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

            try {
                responseString?.let {
                    val jsonResponse = JSONObject(it)
                    val links = jsonResponse.getJSONObject("parse").getJSONArray("links")
                    val unwantedTitlePattern = Regex("ISO \\d+-\\d+")

                    for (i in 0 until links.length()) {
                        val linkObject = links.getJSONObject(i)
                        val title = linkObject.getString("*")

                        // Check if it's an actual article and does not contain a colon
                        if (!title.endsWith("(identifier)") && !title.contains(":") && !unwantedTitlePattern.matches(title)) {
                            titles.add(title)
                            if (titles.size >= maxTitles) break
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                return
                // Handle the JSONException here (e.g., log it, continue to next section, etc.)
            }
            onTitlesFetched(titles.toList()) // Callback with the titles fetched
            section++
            // If you've checked all sections or reached the maximum number of titles, exit the loop
            if (titles.size >= maxTitles) break
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

    private suspend fun fetchHtmlFromUrl(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
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
    }

    fun getFirstParagraphWithArticleNameOrBoldTextFromHtml(url: String, htmlContent: String): String {
        val document: Document = Jsoup.parse(htmlContent)
        val articleName = url.substringAfterLast("/").replace("_", " ")
        val paragraphs = document.select("p")
        var firstParagraphWithArticleName: String? = null
        var finalParagraph: String? = null
        val pattern = Regex("\\[.*?\\]")

        for (paragraph in paragraphs) {
            val text = paragraph.text()
            val boldText = paragraph.select("b").text()

            val plainTextPattern = Regex("^[A-Za-z ]+\$")
            if (text.contains(articleName, ignoreCase = true) && plainTextPattern.matches(boldText)) {
                firstParagraphWithArticleName = text
                break
            }
        }

        // If the paragraph with exact article name is not found, search for bold text
        if (firstParagraphWithArticleName == null) {
            var firstParagraphWithBoldText: String? = null

            // Second loop: Search for the first paragraph with bold text
            for (paragraph in paragraphs) {
                val boldText = paragraph.select("b").text()
                if (boldText.isNotEmpty()) {
                    firstParagraphWithBoldText = paragraph.text()
                    break
                }
            }

            // Select the final paragraph based on whether a paragraph with bold text was found
            finalParagraph = firstParagraphWithBoldText ?: "No relevant content found"
        } else {
            finalParagraph = firstParagraphWithArticleName
        }

        finalParagraph = finalParagraph.replace(pattern,"")

        return finalParagraph
    }

    suspend fun fetchAndProcessHtmlToGetParagraph(url: String): String {
        val htmlContent = fetchHtmlFromUrl(url) ?: return "No content found"
        return getFirstParagraphWithArticleNameOrBoldTextFromHtml(url, htmlContent)
    }
}
