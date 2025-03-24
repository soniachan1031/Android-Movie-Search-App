package com.example.assignment2_valentine_shong.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment2_valentine_shong.databinding.ActivityMainBinding
import com.example.assignment2_valentine_shong.viewmodel.MovieViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val movieViewModel: MovieViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView Adapter
        adapter = MovieAdapter()
        binding.movieList.layoutManager = LinearLayoutManager(this)
        binding.movieList.adapter = adapter

        // Search Button Click Listener
        binding.searchButton.setOnClickListener {
            val query = binding.searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                movieViewModel.searchMovies(query)
            } else {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe Movie List Changes
        movieViewModel.movies.observe(this) { movies ->
            adapter.updateMovies(movies)
        }

        // Implement Pagination (Load More on Scroll)
        binding.movieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0
                ) {
                    movieViewModel.loadMoreMovies()
                    Toast.makeText(this@MainActivity, "Loading More Movies...", Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Observe Total Results and Show Toast Message
        movieViewModel.movies.observe(this) {
            Toast.makeText(this, "Total results: ${movieViewModel.getTotalResults()}", Toast.LENGTH_LONG).show()
        }
    }
}