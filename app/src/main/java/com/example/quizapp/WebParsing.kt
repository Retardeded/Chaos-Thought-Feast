package com.example.quizapp

import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import java.util.regex.Matcher
import java.util.regex.Pattern


class WebParsing(var applicationContext: Context) {

    private lateinit var mCurrentHtml:String
    private lateinit var mUrls:MutableList<String>
    private var currentIndex = 0

    fun isTitleCorrect(url:String, tv: TextView) {

        Ion.with(applicationContext).load(url).asString()
            .setCallback(object :
                FutureCallback<String?> {
                override fun onCompleted(e: Exception?, result: String?) {
                    if (result != null) {
                        tv.setText("true")
                    }
                }
            })
    }

    fun getHtmlFromUrl(url: String, tv: TextView, options: ArrayList<TextView>) {
        Ion.with(applicationContext).load(url).asString()
            .setCallback(object :
                    FutureCallback<String?> {
                override fun onCompleted(e: Exception?, result: String?) {
                    if (result != null) {
                        mCurrentHtml = result
                        currentIndex = 0
                    }
                    else {
                       return
                    }
                    mUrls = parseLinksFromHtmlCode(url, mCurrentHtml)
                    tv.setText(url)
                    setNewOptions(options)
                }
            })
    }

    fun setNewOptions(options: ArrayList<TextView>) {
        for (i in options.indices) {

            val line = mUrls[currentIndex]
            val pattern = "([^/]+\$)"
            val r = Pattern.compile(pattern)
            val m = r.matcher(line)
            if (m.find()) {
                options[i].setText(m.group(0))
                currentIndex++
            }
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
