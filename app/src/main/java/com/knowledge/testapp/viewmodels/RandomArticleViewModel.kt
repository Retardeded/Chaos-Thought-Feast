package com.knowledge.testapp.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.knowledge.testapp.data.Language
import com.knowledge.testapp.utils.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.HashMap

class RandomArticleViewModel : ViewModel() {

    private val lang = UserManager.getUser().language.languageCode
    private val mostPopularPagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("mostPopularPagesWithCategories")
    private val categoriesRef = if (lang == Language.ENGLISH.languageCode) {
        FirebaseDatabase.getInstance().getReference("categories")
    } else {
        FirebaseDatabase.getInstance().getReference("categories$lang")
    }
    private val categoriesTranslationRef = FirebaseDatabase.getInstance().getReference("categories${lang}translation")

    var categoriesTranslation = mutableMapOf<String, String>()

    private val _categories = MutableLiveData<Map<String, List<String>>>()
    val categories: LiveData<Map<String, List<String>>> = _categories

    init {
        if (lang != Language.ENGLISH.languageCode) {
            categoriesTranslations {
                categoriesTranslation.putAll(it)
            }
        }
        fetchCategories()
    }

    private fun fetchCategories() {
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categoryMap = mutableMapOf<String, List<String>>()
                for (categorySnapshot in dataSnapshot.children) {
                    val categories = mutableListOf<String>()
                    for (subcategorySnapshot in categorySnapshot.children) {
                        val subcategoryName = subcategorySnapshot.getValue(String::class.java)
                        subcategoryName?.let {
                            categories.add(it)
                        }
                    }
                    categoryMap[categorySnapshot.key.toString()] = categories
                }
                _categories.value = categoryMap // Update LiveData
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur.
            }
        })
    }

    fun categoriesTranslations(callback: (Map<String, String>) -> Unit) {
        categoriesTranslationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categoryMap = mutableMapOf<String, String>()
                for (categorySnapshot in dataSnapshot.children) {
                    val subcategory = categorySnapshot.key
                    val subcategoryTranslation = categorySnapshot.getValue(String::class.java)
                    if (subcategory != null && subcategoryTranslation != null) {
                        categoryMap.put(subcategory, subcategoryTranslation)
                    }
                }
                System.out.println(categoryMap)
                callback(categoryMap) // Return the list of subcategories
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur.
            }
        })
    }

    suspend fun getRandomWikiEntryInCorrectLanguage(): String? {
        val entries = getRandomConsecutiveWikiEntries(10)
        entries.forEach { title ->
            if (title != null) {
                if (lang == Language.ENGLISH.languageCode) {
                    return title
                } else {
                    val translatedTitle = translateTitleToLanguage(title, lang)
                    if (translatedTitle != null) {
                        if (translatedTitle.isNotEmpty()) {
                            return translatedTitle
                        }
                    }
                }
            }
        }
        return null
    }

    suspend fun translateTitleToLanguage(title: String, languageCode: String): String? = withContext(Dispatchers.IO) {
        val apiUrl = buildWikipediaApiUrl(title, languageCode)
        try {
            URL(apiUrl).openConnection().let { connection ->
                (connection as HttpURLConnection).run {
                    requestMethod = "GET"
                    inputStream.bufferedReader().use { it.readText() }.let { response ->
                        return@let parseTranslationFromResponse(response, languageCode)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TranslateTitle", "Error in translating title: ${e.localizedMessage}")
            null
        }
    }

    private fun buildWikipediaApiUrl(title: String, languageCode: String): String {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        return "https://en.wikipedia.org/w/api.php?action=query&prop=langlinks&titles=$encodedTitle&lllang=$languageCode&format=json"
    }

    private fun parseTranslationFromResponse(response: String, languageCode: String): String? {
        return try {
            val json = JSONObject(response)
            val pages = json.optJSONObject("query")?.optJSONObject("pages")
            val firstPageId = pages?.keys()?.next()
            val langLinks = pages?.optJSONObject(firstPageId)?.optJSONArray("langlinks")

            if (langLinks != null && langLinks.length() > 0) {
                val langLink = langLinks.optJSONObject(0)
                if (langLink.getString("lang") == languageCode) {
                    langLink.optString("*")
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: JSONException) {
            Log.e("ParseTranslation", "JSON parsing error: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getRandomConsecutiveWikiEntries(entriesCount: Int): List<String?> = withContext(Dispatchers.IO) {
        val randomStartIndex = (0 until 8000).random()
        val query = mostPopularPagesRef.orderByKey().startAt(randomStartIndex.toString()).limitToFirst(entriesCount)
        try {
            val dataSnapshot = query.get().await()
            if (dataSnapshot.exists()) {
                return@withContext dataSnapshot.children.map { childSnapshot ->
                    childSnapshot.child("title").value.toString()
                }
            }
        } catch (e: Exception) {
            Log.e("RandomArticleViewModel", "Error fetching entries: ${e.localizedMessage}")
        }

        return@withContext emptyList<String?>()
    }
    suspend fun getRandomArticleFromCategory(category: String): String? = withContext(Dispatchers.IO) {
        try {
            val translatedCategory = if (lang != Language.ENGLISH.languageCode) {
                categoriesTranslation[category] ?: category
            } else {
                category
            }

            val query = mostPopularPagesRef.orderByChild("category").equalTo(translatedCategory)
            val dataSnapshot = query.get().await()

            if (dataSnapshot.exists()) {
                val entries = dataSnapshot.children.mapNotNull { it.child("title").value as? String }.shuffled()

                for (title in entries) {
                    val translatedTitle = if (lang == Language.ENGLISH.languageCode) {
                        title
                    } else {
                        translateTitleToLanguage(title, lang)
                    }

                    if (!translatedTitle.isNullOrEmpty()) {
                        return@withContext translatedTitle
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("RandomArticleVM", "Error fetching article: ${e.localizedMessage}")
        }
        return@withContext null
    }

    suspend fun getArticleByKeyword(keyword: String): String? = withContext(Dispatchers.IO) {
        val urlStr = buildWikipediaApiUrlKeyword(keyword, lang)
        var urlConnection: HttpURLConnection? = null

        try {
            val url = URL(urlStr)
            urlConnection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
            }

            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                urlConnection.inputStream.bufferedReader().use { reader ->
                    val response = reader.readText()
                    return@withContext parseRandomArticleFromKeyword(response)
                }
            } else {
                Log.e("getArticleByKeyword", "HTTP error code: ${urlConnection.responseCode}")
            }
        } catch (e: Exception) {
            Log.e("getArticleByKeyword", "Error fetching article by keyword: ${e.localizedMessage}")
        } finally {
            urlConnection?.disconnect()
        }

        null
    }

    private fun buildWikipediaApiUrlKeyword(keyword: String, languageCode: String): String {
        val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
        return "https://${languageCode}.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encodedKeyword&format=json"
    }

    fun parseRandomArticleFromKeyword(json: String): String? {
        return try {
            val jsonObject = JSONObject(json)
            val searchArray = jsonObject.optJSONObject("query")?.optJSONArray("search")

            searchArray?.let {
                val titles = List(it.length()) { index -> it.optJSONObject(index)?.optString("title") }
                    .filterNotNull()

                titles.randomOrNull()
            }
        } catch (e: JSONException) {
            Log.e("ArticleParsing", "Error parsing JSON for articles: ${e.localizedMessage}")
            null
        }
    }
}

//countTitlesPerCategory()
/*

fun countTitlesPerCategory() {
        val categoryCountMap = HashMap<String, Int>()

        mostPopularPagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val category = childSnapshot.child("category").getValue(String::class.java)
                    category?.let {
                        categoryCountMap[it] = categoryCountMap.getOrDefault(it, 0) + 1
                    }
                }

                // Sort the categories in ascending order based on count
                val sortedCategories = categoryCountMap.entries.sortedBy { it.value }

                // Print the sorted categories and counts to the console
                for ((category, count) in sortedCategories) {
                    println("$category: $count")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }











val mostPopularPagesWithCategoriesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("mostPopularPagesWithCategories")

val uniqueCategories = mutableSetOf<String>()

// Add a ValueEventListener to fetch and process the data
mostPopularPagesWithCategoriesRef.addValueEventListener(object : ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        // Iterate through the children of "mostPopularPagesWithCategories"
        for (childSnapshot in dataSnapshot.children) {
            // Get the "category" value from each child
            val category = childSnapshot.child("category").getValue(String::class.java)
            if (category != null) {
                uniqueCategories.add(category)
            }
        }

        // Print the unique categories to the console
        for (category in uniqueCategories) {
            println(category)
        }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        // Handle any errors here
    }
})

 */