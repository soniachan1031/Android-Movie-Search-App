package com.example.assignment2_valentine_shong.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.assignment2_valentine_shong.R
import com.example.assignment2_valentine_shong.databinding.ActivityMovieDetailsBinding
import com.example.assignment2_valentine_shong.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goBackButton.setOnClickListener {
            finish()
        }

        // Get IMDb ID from Intent
        val imdbID = intent.getStringExtra("IMDB_ID")
        if (!imdbID.isNullOrEmpty()) {
            fetchMovieDetails(imdbID)
        } else {
            binding.movieTitle.text = getString(R.string.movie_not_found)
            binding.moviePlot.text = getString(R.string.no_details_available)
        }
    }

    private fun fetchMovieDetails(imdbID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://www.omdbapi.com/?i=$imdbID&apikey=e41f5847"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("MovieDetailsActivity", "API Error: ${response.code}")
                        return@use
                    }

                    val responseData = response.body?.string()
                    if (responseData != null) {
                        val jsonObject = JSONObject(responseData)

                        withContext(Dispatchers.Main) {
                            try {
                                binding.movieTitle.text = jsonObject.optString("Title", "Unknown Title")
                                binding.movieYear.text = jsonObject.optString("Year", "N/A")
                                binding.movieRuntime.text = jsonObject.optString("Runtime", "N/A")
                                binding.movieGenre.text = jsonObject.optString("Genre", "N/A")
                                binding.moviePlot.text = jsonObject.optString("Plot", "Plot information not available.")

                                // Set IMDb rating in yellow
                                val imdbRating = jsonObject.optString("imdbRating", "N/A")
                                binding.movieImdbRating.text = imdbRating
                                binding.movieImdbRating.setTextColor(getColor(android.R.color.holo_orange_light))

                                val posterUrl = jsonObject.optString("Poster", "N/A")
                                if (posterUrl != "N/A") {
                                    Glide.with(binding.moviePoster.context).load(posterUrl).into(binding.moviePoster)
                                } else {
                                    Glide.with(binding.moviePoster.context).load(Constants.PLACEHOLDER_IMAGE_URL).into(binding.moviePoster)
                                }

                                Log.d("MovieDetailsActivity", "Movie Details Loaded: ${jsonObject.getString("Title")}")
                            } catch (e: Exception) {
                                Log.e("MovieDetailsActivity", "Error parsing JSON: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MovieDetailsActivity", "Error fetching movie details: ${e.message}")
            }
        }
    }
}