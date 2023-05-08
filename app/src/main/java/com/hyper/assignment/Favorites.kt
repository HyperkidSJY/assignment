package com.hyper.assignment

import android.content.Intent
import android.icu.text.Transliterator.Position
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyper.assignment.adapters.MovieListAdapter
import com.hyper.assignment.database.AppDatabase
import com.hyper.assignment.models.Movie
import com.hyper.assignment.utils.SwipeToDeleteCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Favorites : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var appDB: AppDatabase
    private var movies: ArrayList<Movie>? = null
    private lateinit var recyclerView: RecyclerView

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
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDB = AppDatabase.getDatabase(requireContext())

        getFavorites()
    }

    private fun getFavorites() {
        lifecycleScope.launch {
            appDB.movieDao().getFavorites().collect {
                movies = ArrayList(it)
                setupRecyclerView()
            }

        }
    }

    private fun setupRecyclerView() {
        recyclerView = view?.findViewById(R.id.rvMovieLists)!!
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        val movieListAdapter = movies?.let { MovieListAdapter(requireContext(), it) }
        recyclerView.adapter = movieListAdapter

        val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                unFavorite(position)
                Toast.makeText(requireContext(), "unFavorited!!", Toast.LENGTH_SHORT).show()
                getFavorites()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(recyclerView)

        movieListAdapter?.setOnClickListener(object : MovieListAdapter.OnClickListener {
            override fun onClick(position: Int, model: Movie) {
                val intent = Intent(context, MovieDetails::class.java)
                intent.putExtra(Home.EXTRA_MOVIE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    private fun unFavorite(position: Int) {
        lifecycleScope.launch {
            appDB.movieDao().unFavorite(movies!![position].IMDBID)
        }
    }

}