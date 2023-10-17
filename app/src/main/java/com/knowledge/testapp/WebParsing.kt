package com.knowledge.testapp

import android.widget.TextView
import com.knowledge.testapp.utils.ModifyingStrings.Companion.encodedURLToText
import okhttp3.*
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WebParsing() {

    private lateinit var mCurrentHtml:String
    private lateinit var mUrls:ArrayList<String>
    private var currentIndex = 0
    private var currentOverloadedIndex = 0
    private val okHttpClient = OkHttpClient()

    fun isTitleCorrect(url: String, callback: (Boolean) -> Unit) {

        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val result = response.body?.string()
                        if (result != null && !result.contains("Wikipedia does not have an article with this exact name")) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                } finally {
                    response.body?.close()
                }
            }
        })
    }

    fun fetchHtmlFromUrl(url: String, callback: (String) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { responseBody ->
                    if (response.isSuccessful) {
                        val result = responseBody.body?.string()
                        if (result != null) {
                            callback(result) // Return the HTML content
                        }
                    }
                }
            }
        })
    }

    fun getTitlesFromHtml(url: String, htmlContent: String, tv: TextView, callback: (ArrayList<String>) -> Unit) {
        // Process the HTML content
        mCurrentHtml = htmlContent
        System.out.println("link:" + url)
        mUrls = parseLinksFromHtmlCode(url, mCurrentHtml)

        tv.post {
            val lastPart = url.substringAfterLast("/")
            tv.text = lastPart
            callback(mUrls) // Call the callback with mUrls
        }
    }

    fun getFirstParagraphWithArticleNameOrBoldTextFromHtml(url: String, htmlContent: String, tv: TextView) {
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

        tv.post {
            tv.text = finalParagraph
        }
    }

    fun fetchAndProcessHtmlToGetTitles(url: String, tv: TextView, callback: (ArrayList<String>) -> Unit) {
        fetchHtmlFromUrl(url) { htmlContent ->
            getTitlesFromHtml(url, htmlContent, tv, callback)
        }
    }

    fun fetchAndProcessHtmlToGetParagraph(url: String, tv: TextView) {
        fetchHtmlFromUrl(url) { htmlContent ->
            getFirstParagraphWithArticleNameOrBoldTextFromHtml(url, htmlContent, tv)
        }
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
