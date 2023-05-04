package com.hyper.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hyper.assignment.databinding.ActivityMovieDetailsBinding
import com.hyper.assignment.models.Movie
import com.squareup.picasso.Picasso

class MovieDetails : AppCompatActivity() {

    private lateinit var binding : ActivityMovieDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var movieDetail : Movie? = null

        if(intent.hasExtra(Home.EXTRA_MOVIE_DETAILS)){
            movieDetail = intent.getSerializableExtra(Home.EXTRA_MOVIE_DETAILS) as Movie
        }

        if(movieDetail != null){
            setSupportActionBar(binding.toolbarMovieDetails)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = movieDetail.Title

            binding.toolbarMovieDetails.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            Picasso.get().load(movieDetail.MoviePoster).into(binding.ivMovieImage)
            binding.tvSummary.text  = "Summary: " + movieDetail.Summary
            binding.tvGenres.text = "Genres: " + movieDetail.Genres
            binding.tvRatings.text = "Ratings: " + movieDetail.Rating
            binding.tvRuntime.text = "Runtime: " + movieDetail.Runtime
            binding.tvDirector.text = "Director: " + movieDetail.Director
            binding.tvWriters.text = "Writers: " + movieDetail.Writers
            binding.tvCast. text = "Cast: " + movieDetail.Cast
        }
    }
}