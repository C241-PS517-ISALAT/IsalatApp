package com.isalatapp.api

import com.isalatapp.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

object GitHubApiHelper {

    private val SUBFOLDERS = ('A'..'Z').toList()

    fun getGithubFiles(callback: (List<Pair<String, List<String>>>) -> Unit) {
        val client = OkHttpClient()
        val fileUrls = ConcurrentHashMap<String, MutableList<String>>()
        var pendingRequests = SUBFOLDERS.size

        SUBFOLDERS.forEach { char ->
            val request = buildRequest(char.toString())
            client.newCall(request).enqueue(buildCallback(char.toString(), fileUrls) {
                synchronized(this) {
                    pendingRequests--
                    if (pendingRequests == 0) {
                        callback(fileUrls.map { it.key to it.value })
                    }
                }
            })
        }
    }

    private fun buildRequest(char: String): Request {
        return Request.Builder()
            .url("${BuildConfig.AUTH_API_URL}/dataset/$char")
            .build()
    }

    private fun buildCallback(
        char: String,
        fileUrls: ConcurrentHashMap<String, MutableList<String>>,
        onComplete: () -> Unit
    ): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onComplete()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        handleSuccessfulResponse(response, char, fileUrls)
                    } else {
                        println("Unexpected code $response")
                    }
                }
                onComplete()
            }
        }
    }

    private fun handleSuccessfulResponse(
        response: Response,
        char: String,
        fileUrls: ConcurrentHashMap<String, MutableList<String>>
    ) {
        val jsonObject = JSONObject(response.body!!.string())
        val urlsList = mutableListOf<String>()
        jsonObject.keys().forEach {
            val fileUrl = jsonObject.getString(it)
            urlsList.add(fileUrl)
        }
        fileUrls[char] = urlsList
    }
}
