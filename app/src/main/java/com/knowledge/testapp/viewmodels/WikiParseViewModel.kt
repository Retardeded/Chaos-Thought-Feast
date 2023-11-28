package com.knowledge.testapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knowledge.testapp.utils.ModifyingStrings.Companion.encodedURLToText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WikiParseViewModel: ViewModel() {
    private val _options = MutableLiveData<List<String>>()
    val options: LiveData<List<String>> = _options

    private lateinit var mCurrentHtml:String
    private lateinit var mUrls:ArrayList<String>
    private var currentIndex = 0
    private var currentOverloadedIndex = 0
    private val okHttpClient = OkHttpClient()

    fun fetchTitles(articleUrl: String) {
        viewModelScope.launch {
            val titles = fetchAndProcessHtmlToGetTitles(articleUrl)
            _options.value = titles
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

    private fun getTitlesFromHtml(url: String, htmlContent: String): List<String> {
        // Process the HTML content
        mCurrentHtml = htmlContent
        System.out.println("link:" + url)
        mUrls = parseLinksFromHtmlCode(url, mCurrentHtml)

        return (mUrls)

        /*
        tv.post {
            val lastPart = url.substringAfterLast("/")
            tv.text = lastPart
            callback(mUrls) // Call the callback with mUrls
        }

         */
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

    private suspend fun fetchAndProcessHtmlToGetTitles(url: String): List<String> {
        val htmlContent = fetchHtmlFromUrl(url) ?: return emptyList()
        return getTitlesFromHtml(url, htmlContent)
    }

    suspend fun fetchAndProcessHtmlToGetParagraph(url: String): String {
        val htmlContent = fetchHtmlFromUrl(url) ?: return "No content found"
        return getFirstParagraphWithArticleNameOrBoldTextFromHtml(url, htmlContent)
    }

    fun parseLinksFromHtmlCode(baseUrl: String, code: String?): ArrayList<String> {
        val urls: MutableList<String> = ArrayList()
        val patternLinkTag: Pattern =
            Pattern.compile("(?i)(<a.*?href=[\"'])(.*?)([\"'].*?>)(.*?)(</a>)")
        val matcherWebpageHtmlCode: Matcher = patternLinkTag.matcher(code)
        var parsedUrl: StringBuilder
        while (matcherWebpageHtmlCode.find() && urls.size < 200) {
            parsedUrl = StringBuilder().append(
                    matcherWebpageHtmlCode.group(2)
            )
            if (parsedUrl.toString().startsWith("#") || parsedUrl.toString().contains(":") || parsedUrl.toString().contains(";") || parsedUrl.toString().contains("#") || parsedUrl.toString().contains("&") ) {
                continue
            }
            if (parsedUrl.toString().startsWith("/")) {
                parsedUrl.insert(0, baseUrl)
                val encodedUrl = encodedURLToText(parsedUrl.toString())
                val lastPart = encodedUrl.substringAfterLast("/")
                urls.add(lastPart)
            }
        }

        return ArrayList(urls.subList(3, urls.size))
    }
}
