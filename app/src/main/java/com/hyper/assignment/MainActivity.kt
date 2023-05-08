package com.hyper.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hyper.assignment.database.AppDatabase
import com.hyper.assignment.databinding.ActivityMainBinding
import com.hyper.assignment.models.Movie
import com.hyper.assignment.models.MovieList
import com.hyper.assignment.network.MovieListService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var appDB: AppDatabase
    private var movies: ArrayList<Movie>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDB = AppDatabase.getDatabase(this)
        isMovieListAvailable()

        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(Home())
                R.id.profile -> replaceFragment(Profile())
                R.id.favorites -> replaceFragment(Favorites())

                else ->{
                }
            }

            true

        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.detach(fragment)
        fragmentTransaction.attach(fragment)
        fragmentTransaction.commit()
    }

    private fun isMovieListAvailable(){
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("http://task.auditflo.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service : MovieListService = retrofit.create(MovieListService::class.java)
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
                Log.e("error" , t.message.toString())
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
}