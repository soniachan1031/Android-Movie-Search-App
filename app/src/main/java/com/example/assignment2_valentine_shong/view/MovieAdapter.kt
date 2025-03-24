package com.example.assignment2_valentine_shong.view

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment2_valentine_shong.R
import com.example.assignment2_valentine_shong.databinding.ItemMovieBinding
import com.example.assignment2_valentine_shong.model.MovieModel
import com.example.assignment2_valentine_shong.utils.Constants

class MovieAdapter : ListAdapter<MovieModel, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieModel) {
            binding.movieTitle.text = movie.title
            binding.movieStudio.text = "Studio: ${movie.studio}"
            binding.movieRating.text = "Rating: ${movie.imdbRating}"
            binding.movieYear.text = "Year: ${movie.year}"

            // Debugging Log for Poster URL
            Log.d("MovieAdapter", "Loading Poster URL: ${movie.poster}")

            // Load Movie Poster Using Glide
            val posterUrl = if (!movie.poster.isNullOrEmpty() && movie.poster != "N/A") movie.poster
            else null

            Glide.with(binding.moviePoster.context)
                .load(posterUrl)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .centerCrop()
                .into(binding.moviePoster)


            // Handle Click Event to Open MovieDetailsActivity
            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra("IMDB_ID", movie.imdbID)
                }
                Log.d("MovieAdapter", "Opening Movie Details for IMDb ID: ${movie.imdbID}")
                context.startActivity(intent)
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<MovieModel>() {
        override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
            return oldItem.imdbID == newItem.imdbID // Use IMDb ID for search
        }

        override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
            return oldItem == newItem
        }
    }

    fun updateMovies(newMovies: List<MovieModel>) {
        submitList(newMovies.toList()) // Ensures RecyclerView updates correctly
    }
}