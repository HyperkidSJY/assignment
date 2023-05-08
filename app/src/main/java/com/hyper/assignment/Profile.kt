package com.hyper.assignment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.hyper.assignment.database.AppDatabase
import com.hyper.assignment.models.Movie
import com.hyper.assignment.models.MovieList
import com.hyper.assignment.network.MovieListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDB = AppDatabase.getDatabase(requireContext())
        isMovieListAvailable()
    }


    private fun isMovieListAvailable(){
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("http://task.auditflo.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service : MovieListService = retrofit.create<MovieListService>(MovieListService::class.java)
        val listCall : Call<MovieList> = service.getMovieList()



        listCall.enqueue(object  : Callback<MovieList> {
            override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                if(!response.isSuccessful){
                    Log.e("Error", "Server Response : ${response.code()} : ${response.message()}")
                }
                val movieList  = response.body()
                movies  = movieList?.movieList as ArrayList<Movie>?
                Log.i("movies" , "$movies")
                writeData()
            }

            override fun onFailure(call: Call<MovieList>, t: Throwable) {
                Log.e("error" , t!!.message.toString())
            }

        })
    }
    private fun writeData(){
        if(!movies.isNullOrEmpty()){
            lifecycleScope.launch {
                for(i in movies!!){
                    appDB.movieDao().insert(i)
                }
            }
        }
    }

    private lateinit var appDB : AppDatabase
    private var movies : ArrayList<Movie>? = null
}