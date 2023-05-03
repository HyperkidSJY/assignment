package com.hyper.assignment.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieList(
    @SerializedName("Movie List")
    val movieList : List<Movie>
): Serializable