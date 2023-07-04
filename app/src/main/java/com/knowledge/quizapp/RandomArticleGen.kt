import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.random.Random

class RandomArticleViewModel : ViewModel() {

    suspend fun getRandomArticle(): String? = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("https://en.wikipedia.org/w/api.php?action=query&list=random&rnnamespace=0&format=json")
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
        var urlConnection: HttpURLConnection? = null
        try {
            var urlStr = "https://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmlimit=max&cmnamespace=0&format=json"
            val encodedCategory = URLEncoder.encode(category, "UTF-8")
            urlStr += "&cmtitle=$encodedCategory"

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

                return@withContext parseRandomArticleFromCategory(response.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }

        return@withContext null
    }

    suspend fun getArticleByKeyword(keyword: String): String? = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        try {
            val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
            val urlStr = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=$encodedKeyword&format=json"
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
                return randomObject.getString("title").replace(" ", "_")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseRandomArticleFromCategory(response: String): String? {
        try {
            val jsonObject = JSONObject(response)
            val queryObject = jsonObject.getJSONObject("query")
            val categoryMembersArray = queryObject.getJSONArray("categorymembers")
            if (categoryMembersArray.length() > 0) {
                val randomIndex = (0 until categoryMembersArray.length()).random()
                val randomObject = categoryMembersArray.getJSONObject(randomIndex)
                return randomObject.getString("title")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    suspend fun getRandomCategory(): String? = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("https://en.wikipedia.org/w/api.php?action=query&generator=random&grnnamespace=14&prop=categories&format=json")
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

                return@withContext parseRandomCategory(response.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }

        return@withContext null
    }

    private fun parseRandomCategory(response: String): String? {
        try {
            val jsonObject = JSONObject(response)
            val queryObject = jsonObject.getJSONObject("query")
            val pagesObject = queryObject.getJSONObject("pages")

            // Get a random page ID from the available pages
            val pageIds = pagesObject.keys().asSequence().toList()
            val randomPageId = pageIds[Random.nextInt(pageIds.size)]

            val randomPageObject = pagesObject.getJSONObject(randomPageId)
            val categoriesArray = randomPageObject.getJSONArray("categories")

            // Get a random category from the available categories
            if (categoriesArray.length() > 0) {
                val randomIndex = (0 until categoriesArray.length()).random()
                val randomCategoryObject = categoriesArray.getJSONObject(randomIndex)
                val categoryName = randomCategoryObject.getString("title")

                // Extract the category name without the "Category:" prefix
                return categoryName.removePrefix("Category:")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}