import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

    fun parseRandomArticleTitle(response: String): String? {
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
}