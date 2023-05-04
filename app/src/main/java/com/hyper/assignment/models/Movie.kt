package com.hyper.assignment.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "movies_table")
data class Movie(
    val Title : String?,
    val Year : String?,
    val Summary : String?,

    @SerializedName("Short Summary")
    val ShortSummary : String?,

    val Genres : String?,

    @PrimaryKey(autoGenerate = false)
    val IMDBID : String,
    val Runtime : String?,

    @SerializedName("YouTube Trailer")
    val YouTubeTrailer : String?,

    val Rating : String?,

    @SerializedName("Movie Poster")
    val MoviePoster : String?,
    val Director : String?,
    val Writers : String?,
    val Cast : String?,
    val isFavorite : Boolean = false
) : Serializable