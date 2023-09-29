package com.knowledge.testapp

import android.content.Context
import android.widget.TextView
import com.knowledge.testapp.utils.ModifyingStrings
import com.knowledge.testapp.utils.ModifyingStrings.Companion.encodedURLToText
import okhttp3.*
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

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
    fun getHtmlFromUrl(url: String, tv: TextView, callback: (ArrayList<String>) -> Unit) {
        println("inner webparse url:: $url")

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
                            mCurrentHtml = result
                            mUrls = parseLinksFromHtmlCode(url, mCurrentHtml)
                            tv.post {
                                val lastPart = url.substringAfterLast("/")
                                tv.text = lastPart
                                callback(mUrls) // Call the callback with mUrls
                            }
                        }
                    }
                }
            }
        })
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
