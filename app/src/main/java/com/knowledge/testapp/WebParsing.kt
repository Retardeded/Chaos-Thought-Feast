package com.knowledge.testapp

import android.content.Context
import android.widget.TextView
import okhttp3.*
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern


class WebParsing(var applicationContext: Context) {

    private lateinit var mCurrentHtml:String
    private lateinit var mUrls:MutableList<String>
    private var currentIndex = 0
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
            }
        })
    }

    fun getHtmlFromUrl(url: String, tv: TextView, options: ArrayList<TextView>) {

        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val result = response.body?.string()
                    if (result != null) {
                        // Process the result
                        mCurrentHtml = result
                        currentIndex = 0

                        mUrls = parseLinksFromHtmlCode(url, mCurrentHtml)
                        // Use runOnUiThread to update the UI safely
                        tv.post {
                            tv.setText(url)
                            setNextLinks(options)
                        }
                    }
                }
            }
        })
    }

    fun setNextLinks(options: ArrayList<TextView>) {

        if(currentIndex + options.size >= mUrls.size)
            return

        for (i in options.indices) {
            val line = mUrls[currentIndex]
            val pattern = "([^/]+\$)"
            val r = Pattern.compile(pattern)
            val m = r.matcher(line)
            if (m.find()) {
                options[i].setText(m.group(0))
            }
            currentIndex++
        }
    }

    fun setPreviousLinks(options: ArrayList<TextView>) {

        if(currentIndex - 2*options.size < 0)
            return

        currentIndex -= 2*options.size

        for (i in options.indices) {
            val line = mUrls[currentIndex]
            val pattern = "([^/]+\$)"
            val r = Pattern.compile(pattern)
            val m = r.matcher(line)
            if (m.find()) {
                options[i].setText(m.group(0))
            }
            currentIndex++
        }
    }

    fun parseLinksFromHtmlCode(baseUrl: String, code: String?): MutableList<String> {
        var baseUrl = baseUrl
        val urls: MutableList<String> = ArrayList()
        var lang:String = ""
        if (baseUrl.contains("https://pl.wikipedia.org")) {
            baseUrl =
                "https://pl.wikipedia.org"
            lang = "pl"
        }
        else if (baseUrl.contains("https://en.wikipedia.org")) {
            baseUrl =
                "https://en.wikipedia.org"
            lang = "en"
        }
        val patternLinkTag: Pattern =
            Pattern.compile("(?i)(<a.*?href=[\"'])(.*?)([\"'].*?>)(.*?)(</a>)")
        val matcherWebpageHtmlCode: Matcher = patternLinkTag.matcher(code)
        val webpageUrlProtocol = baseUrl.substring(0, baseUrl.indexOf("//"))
        val matcherWebpageUrlBase: Matcher = Pattern.compile("^.+/").matcher(baseUrl)
        var webpageUrlBase = ""
        if (matcherWebpageUrlBase.find()) {
            webpageUrlBase = matcherWebpageUrlBase.group()
        }
        var parsedUrl: StringBuilder
        while (matcherWebpageHtmlCode.find()) {
            parsedUrl = StringBuilder().append(
                    matcherWebpageHtmlCode.group(2)
            )
            //|| !parsedUrl.toString().startsWith("https://" + lang)
            if (parsedUrl.toString().startsWith("#") || parsedUrl.toString().contains(":") || parsedUrl.toString().contains(";") || parsedUrl.toString().contains("#") || parsedUrl.toString().contains("&") ) {
                continue
            } else if (!parsedUrl.toString().startsWith("http")) {
                if (parsedUrl.toString().startsWith("//")) {
                    parsedUrl.insert(0, webpageUrlProtocol)
                } else if (parsedUrl.toString().startsWith("/")) {
                    parsedUrl.insert(0, baseUrl)
                } else {
                    parsedUrl.insert(0, webpageUrlBase)
                }
            }

            if(parsedUrl.toString().startsWith("https://" + lang))
            {
                urls.add(parsedUrl.toString())
            }
        }

        return urls
    }

}
