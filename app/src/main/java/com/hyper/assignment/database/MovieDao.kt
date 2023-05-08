package com.hyper.assignment.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyper.assignment.models.Movie
import kotlinx.coroutines.flow.Flow


@Dao
interface MovieDao {
    @Query("SELECT * FROM movies_table")
    fun getAll(): Flow<List<Movie>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movieEntity: Movie)

    @Delete
    suspend fun delete(movieEntity: Movie)

    @Query("UPDATE movies_table SET isFavorite = 1 WHERE IMDBID LIKE :IMDBID")
    suspend fun addFavorite(IMDBID: String)

    @Query("SELECT * FROM movies_table WHERE isFavorite LIKE 1")
    fun getFavorites(): Flow<List<Movie>>

    @Query("UPDATE movies_table SET isFavorite = 0 WHERE IMDBID LIKE :IMDBID")
    suspend fun unFavorite(IMDBID: String)
}