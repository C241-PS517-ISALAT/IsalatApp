package com.isalatapp.api


import okhttp3.*
import org.json.JSONArray
import java.io.IOException

object  GitHubApiHelper {

    private const val BASE_GITHUB_API_URL = "https://api.github.com/repos/C241-PS517-ISALAT/C241-PS517-ISALAT/contents/"
    private const val GITHUB_TOKEN = "" // Tokennya personal user github
    private val SUBFOLDERS = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    fun getGithubFiles(callback: (List<String>) -> Unit) {
        val client = OkHttpClient()
        val fileUrls = mutableListOf<String>()

        var pendingRequests = SUBFOLDERS.size

        SUBFOLDERS.forEach { folder ->
            val request = Request.Builder()
                .url("$BASE_GITHUB_API_URL$folder?ref=master")
                .header("Authorization", "token $GITHUB_TOKEN")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    synchronized(this) {
                        pendingRequests--
                        if (pendingRequests == 0) {
                            callback(fileUrls)
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            println("Unexpected code $response")
                        } else {
                            val jsonArray = JSONArray(response.body!!.string())
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val fileUrl = jsonObject.getString("download_url")
                                fileUrls.add(fileUrl)
                            }
                        }
                    }
                    synchronized(this) {
                        pendingRequests--
                        if (pendingRequests == 0) {
                            callback(fileUrls)
                        }
                    }
                }
            })
        }
    }
}
