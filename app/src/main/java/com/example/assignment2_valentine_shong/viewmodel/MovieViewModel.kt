package com.example.assignment2_valentine_shong.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2_valentine_shong.model.MovieModel
import com.example.assignment2_valentine_shong.utils.ApiClient
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val _movies = MutableLiveData<List<MovieModel>>()
    val movies: LiveData<List<MovieModel>> get() = _movies
    private var currentPage = 1
    private var totalResults = 0
    private var currentQuery: String? = null

    fun searchMovies(query: String) {
        currentQuery = query
        currentPage = 1
        loadMovies(query, currentPage)
    }

    fun loadMoreMovies() {
        currentQuery?.let {
            loadMovies(it, ++currentPage)
        }
    }

    private fun loadMovies(query: String, page: Int) {
        viewModelScope.launch {
            val (newMovies, totalResults) = ApiClient.fetchMovies(query, page)
            this@MovieViewModel.totalResults = totalResults
            if (page == 1) {
                _movies.value = newMovies
            } else {
                _movies.value = _movies.value.orEmpty() + newMovies.orEmpty()
            }
        }
    }

    fun getTotalResults(): Int {
        return totalResults
    }
}