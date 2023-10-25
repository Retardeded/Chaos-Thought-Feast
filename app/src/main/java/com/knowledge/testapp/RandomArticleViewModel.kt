import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.knowledge.testapp.QuizValues
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
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RandomArticleViewModel : ViewModel() {

    private val lang = QuizValues.USER!!.languageCode.lowercase(Locale.ROOT)
    private val mostPopularPagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("mostPopularPagesWithCategories")
    private val categoriesRef = FirebaseDatabase.getInstance().getReference("categories")
    var categories = mutableMapOf<String, List<String>>()

    init {
        getCategoriesWithSubcategories {
            categories.putAll(it)
        }
    }
    fun getCategoriesWithSubcategories(callback: (Map<String, List<String>>) -> Unit) {
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
                callback(categoryMap) // Return the list of subcategories
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur.
            }
        })
    }

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

    suspend fun getRandomWikiEntryInCorrectLanguage(): String? {
        // Get a random English article title
        val title = getRandomWikiEntry()

        if (lang == "EN") {
            return title
        }

        val translatedTitle = title?.let { translateTitleToLanguage(it, lang) }
        return translatedTitle
    }

    suspend fun translateTitleToLanguage(title: String, languageCode: String): String? {
        return withContext(Dispatchers.IO) {
            val apiUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=langlinks&titles=${title}&lllang=${languageCode}&format=json"
            var translation = ""

            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()

                val json = JSONObject(response)
                val query = json.optJSONObject("query")
                val pages = query?.optJSONObject("pages")

                val firstPageId = pages?.keys()?.next()
                val page = pages?.optJSONObject(firstPageId)

                val langLinks = page?.optJSONArray("langlinks")
                langLinks?.let { links ->
                    for (i in 0 until links.length()) {
                        val langLink = links.optJSONObject(i)
                        if (langLink.getString("lang") == languageCode) {
                            translation = langLink.optString("*")
                            return@let langLink.optString("*")
                        }
                    }
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext translation  // Return null if the translation is not found
        }
    }

    suspend fun getRandomWikiEntry(): String? = suspendCoroutine { continuation ->
        try {

            //countTitlesPerCategory()
            /*
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


            val randomIndex = (0 until 8000).random()

            // Form the reference to the random entry
            val randomEntryRef = mostPopularPagesRef.child(randomIndex.toString())

            // Fetch the random entry's title
            randomEntryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get the random entry
                        val title = dataSnapshot.child("title").value.toString()
                        //Toast.makeText()
                        continuation.resume(title)
                    } else {
                        // Handle the case where the random index doesn't exist
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors during the query
                    continuation.resume(null)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(null)
        }
    }


    suspend fun getRandomArticle(): String? = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("https://$lang.wikipedia.org/w/api.php?action=query&list=random&rnnamespace=0&format=json")
            urlConnection = url.openConnection() as HttpURLConnection
            val responseCode = urlConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()

                println(response.toString()) // Print the response
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                return@withContext parseRandomArticleTitle(response.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }

        return@withContext null
    }
    suspend fun getRandomArticleFromCategory(category: String): String? = withContext(Dispatchers.IO) {

        try {
            // Query Firebase to get all entries with the specified category
            val query = mostPopularPagesRef.orderByChild("category").equalTo(category)
            val dataSnapshot = query.get().await()

            if (dataSnapshot.exists()) {
                // Convert the dataSnapshot to a list of entries
                val entries = mutableListOf<Map<String, Any>>()
                for (childSnapshot in dataSnapshot.children) {
                    val entry = childSnapshot.value as? Map<String, Any>
                    entry?.let { entries.add(it) }
                }

                // Select a random entry from the list
                val randomEntry = entries.random()

                // Return the title of the random entry
                return@withContext randomEntry["title"]?.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }

    suspend fun getArticleByKeyword(keyword: String): String? = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        try {
            val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
            val urlStr = "https://$lang.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encodedKeyword&format=json"
            val url = URL(urlStr)
            urlConnection = url.openConnection() as HttpURLConnection
            val responseCode = urlConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                return@withContext parseRandomArticleFromKeyword(response.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }

        return@withContext null
    }

    fun parseRandomArticleFromKeyword(json: String): String? {
        val articleTitles = mutableListOf<String>()

        try {
            val jsonObject = JSONObject(json)
            val queryObject = jsonObject.optJSONObject("query")
            val searchArray = queryObject?.optJSONArray("search")

            if (searchArray != null) {
                for (i in 0 until searchArray.length()) {
                    val item = searchArray.optJSONObject(i)
                    val title = item?.optString("title")
                    if (title != null) {
                        articleTitles.add(title)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Return a random title from the list, or null if the list is empty
        return if (articleTitles.isNotEmpty()) {
            articleTitles.random()
        } else {
            null
        }
    }

    private fun parseRandomArticleTitle(response: String): String? {
        try {
            val jsonObject = JSONObject(response)
            val queryObject = jsonObject.getJSONObject("query")
            val randomArray = queryObject.getJSONArray("random")
            if (randomArray.length() > 0) {
                val randomObject = randomArray.getJSONObject(0)
                return randomObject.getString("title")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}