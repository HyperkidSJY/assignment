package com.hyper.assignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyper.assignment.adapters.MovieListAdapter
import com.hyper.assignment.database.AppDatabase
import com.hyper.assignment.models.Movie
import com.hyper.assignment.models.MovieList
import com.hyper.assignment.network.MovieListService
import com.hyper.assignment.utils.SwipeGesture
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


class Home : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView : RecyclerView
    private lateinit var appDB : AppDatabase
    private var movies : ArrayList<Movie>? = null



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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDB = AppDatabase.getDatabase(requireContext())

        isMovieListAvailable()
        readData()
    }

    private fun isMovieListAvailable(){
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("http://task.auditflo.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service : MovieListService = retrofit.create<MovieListService>(MovieListService::class.java)
        val listCall : Call<MovieList> = service.getMovieList()

        listCall.enqueue(object  : Callback<MovieList>{
            override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                if(!response.isSuccessful){
                    Log.e("Error", "Server Response : ${response.code()} : ${response.message()}")
                }
                val movieList  = response.body()
                movies  = movieList?.movieList as ArrayList<Movie>?
                writeData()
            }

            override fun onFailure(call: Call<MovieList>, t: Throwable) {
                Log.e("error" , t!!.message.toString())
            }

        })
    }

    private fun setupRecyclerView(){
        GlobalScope.launch(Dispatchers.Main){
            recyclerView = view?.findViewById(R.id.rvMovieLists)!!
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(true)
            val movieListAdapter = movies?.let { MovieListAdapter(requireContext(), it) }
            recyclerView.adapter = movieListAdapter

            swipeGestures(recyclerView)
            if(recyclerView.adapter?.itemCount == 0){
                isMovieListAvailable()
            }
        }

    }

    private fun writeData(){
        GlobalScope.launch(Dispatchers.IO){
            for(i in movies!!){
                appDB.movieDao().insert(i)
            }
        }
    }
    private fun readData(){
        GlobalScope.launch{
            movies = appDB.movieDao().getAll() as ArrayList<Movie>
            setupRecyclerView()
        }
    }

    private fun deleteMoviesByPosition(position : Int) {
        GlobalScope.launch{
            appDB.movieDao().delete(movies!![position])
        }
    }





    private fun swipeGestures(itemRv : RecyclerView){
        val swipeGesture=object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deleteMoviesByPosition(position)
                        readData()
                        itemRv.adapter?.notifyItemRemoved(position)
                    }
                    ItemTouchHelper.RIGHT -> {

                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(itemRv)
    }
}





