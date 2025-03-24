package com.example.assignment2_valentine_shong.utils

import android.util.Log
import com.example.assignment2_valentine_shong.model.MovieModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object ApiClient {
    suspend fun fetchMovies(query: String, page: Int = 1): Pair<List<MovieModel>?, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://www.omdbapi.com/?s=$query&page=$page&apikey=e41f5847"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful)
                        throw Exception("HTTP Error: ${response.code}")

                    response.body?.string()?.let {
                        val jsonObject = JSONObject(it)
                        val totalResults = jsonObject.optInt("totalResults", 0)
                        val jsonArray = jsonObject.optJSONArray("Search") ?:
                            return@let Pair(null, totalResults) // Return null if no movies found

                        val movies = List(jsonArray.length()) { i ->
                            val item = jsonArray.getJSONObject(i)
                            val movieId = item.getString("imdbID")
                            val details = fetchMovieDetails(movieId)

                            MovieModel(
                                title = item.getString("Title"),
                                studio = details?.optString("Production", "N/A") ?: "N/A",
                                imdbRating = details?.optString("imdbRating", "N/A") ?: "N/A",
                                year = item.getString("Year"),
                                poster = item.optString("Poster", "N/A"),
                                imdbID = item.getString("imdbID")
                            )
                        }
                        return@let Pair(movies, totalResults)
                    } ?: Pair(null, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Pair(null, 0)
            }
        }
    }

    private suspend fun fetchMovieDetails(movieId: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://www.omdbapi.com/?i=$movieId&apikey=e41f5847"
                Log.d("ApiClient", "Fetching Movie Details: $url") // Debug Log

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("ApiClient", "API Error: ${response.code}")
                        return@use null
                    }
                    response.body?.string()?.let { JSONObject(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ApiClient", "Exception: ${e.message}")
                null
            }
        }
    }
}