package com.hyper.assignment.network

import com.hyper.assignment.models.MovieList
import retrofit2.Call
import retrofit2.http.GET

interface MovieListService {

    @GET("1.json")
    fun getMovieList() :Call<MovieList>

}