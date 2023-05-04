package com.hyper.assignment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.coroutines.withContext
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
        
        readData()

    }



    private suspend fun setupRecyclerView(){
            withContext(Dispatchers.Main){
                recyclerView = view?.findViewById(R.id.rvMovieLists)!!
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.setHasFixedSize(true)
                val movieListAdapter = movies?.let { MovieListAdapter(requireContext(), it) }
                recyclerView.adapter = movieListAdapter



                movieListAdapter?.setOnClickListener(object : MovieListAdapter.OnClickListener{
                    override fun onClick(position: Int, model: Movie) {
                        val intent = Intent(context,MovieDetails::class.java)
                        intent.putExtra(EXTRA_MOVIE_DETAILS,model)
                        startActivity(intent)
                    }
                } )
                swipeGestures(recyclerView)

            }
    }

    private fun readData(){
        GlobalScope.launch{
            movies = appDB.movieDao().getAll() as ArrayList<Movie>
            setupRecyclerView()
        }


    }

    private fun deleteMoviesByPosition(position : Int) {
        GlobalScope.launch(Dispatchers.IO){
            appDB.movieDao().delete(movies!![position])
        }
    }


    private fun addFavorites(position: Int){

        GlobalScope.launch(Dispatchers.IO) {
            appDB.movieDao().addFavorite(movies!![position].IMDBID)
        }
    }

    private fun swipeGestures(itemRv : RecyclerView){
        val swipeGesture=object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deleteMoviesByPosition(position)
                        Toast.makeText(requireContext(),"Deleted!!",Toast.LENGTH_SHORT).show()
                        readData()
                        recyclerView.adapter?.notifyItemRemoved(position)

                    }
                    ItemTouchHelper.RIGHT -> {
                        addFavorites(position)
                        Toast.makeText(requireContext(),"added to Favorites",Toast.LENGTH_SHORT).show()
                        readData()
                        recyclerView.adapter?.notifyItemChanged(position)

                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(itemRv)
    }

    companion object{
        var EXTRA_MOVIE_DETAILS = "extra_movie_details"
    }
}





