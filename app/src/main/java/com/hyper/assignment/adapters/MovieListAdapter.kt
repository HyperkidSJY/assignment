package com.hyper.assignment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyper.assignment.database.AppDatabase
import com.hyper.assignment.databinding.ListItemBinding
import com.hyper.assignment.models.Movie
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieListAdapter(private  val context : Context, private val list: ArrayList<Movie>)
    : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {

    class ViewHolder(binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            val ivMovieImage = binding.ivMovieImage
            val tvTitle = binding.tvTitle
            val tvRuntime = binding.tvRuntime
            val tvCast = binding.tvCast
            val tvYear = binding.tvYear
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        Picasso.get().load(model.MoviePoster).into(holder.ivMovieImage)
        holder.tvTitle.text = model.Title
        holder.tvCast.text = model.Cast
        holder.tvRuntime.text = model.Runtime
        holder.tvYear.text = model.Year
    }

    override fun getItemCount(): Int {
        return list.size
    }



}