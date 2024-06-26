package com.isalatapp.helper.repository

import android.util.Log
import com.isalatapp.api.ApiClient
import com.isalatapp.api.ApiConfig
import com.isalatapp.helper.data.NewsItem
import com.isalatapp.helper.response.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsRepository {
    fun getNews(
        onSuccess: (List<NewsItem>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val apiKey = ApiConfig.API_KEY
        val category = "health"
        val language = "en"

        ApiClient.newsApiService.searchNews("", category, language, apiKey)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        Log.d("NewsRepository", "Raw articles response: ${response.body()}")
                        val newsList = articles.mapNotNull { article ->
                            if (article.title.isNotEmpty() && !article.urlToImage.isNullOrEmpty() && article.url.isNotEmpty()) {
                                NewsItem(
                                    article.title,
                                    article.description,
                                    article.urlToImage,
                                    article.url
                                )
                            } else {
                                null
                            }
                        }
                        Log.d("NewsRepository", "News fetched successfully: $newsList")
                        onSuccess(newsList)
                    } else {
                        Log.e(
                            "NewsRepository",
                            "Failed to fetch news: ${response.errorBody()?.string()}"
                        )
                        onFailure("Failed to fetch news")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e("NewsRepository", "API call failed: ${t.message}")
                    onFailure(t.message ?: "Unknown error")
                }
            })
    }
}


