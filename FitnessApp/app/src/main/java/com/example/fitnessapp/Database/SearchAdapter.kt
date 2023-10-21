package com.example.fitnessapp.Database

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Activities.ExercisesDescriptionActivity
import com.example.fitnessapp.R
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(private val exerciseList : ArrayList<WorkoutExercisesPreview>, private val context : Activity, private val idList : ArrayList<String>, private val name : String, private val workout : String, private val edit : String) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_list, parent, false)

        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        val exercise : WorkoutExercisesPreview = exerciseList[position]
        holder.name.text = exercise.name
        holder.equipment.text = exercise.equipment
        holder.muscles.text = exercise.muscles
        Glide.with(holder.itemView.context).load(exercise.image_1).circleCrop().skipMemoryCache(true).into(holder.image)

        holder.description.setOnClickListener {
            val intent = Intent(context, ExercisesDescriptionActivity::class.java)
            intent.putExtra("id", idList[position])
            intent.putExtra("name", name)
            intent.putExtra("workout", workout)
            intent.putExtra("edit", edit)
            intent.putExtra("check", "a")
            context.startActivity(intent)
            context.finish()
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val muscles : TextView = itemView.findViewById(R.id.search_muscles)
        val equipment : TextView = itemView.findViewById(R.id.search_equipment)
        val name : TextView = itemView.findViewById(R.id.search_name)
        val image : ImageView = itemView.findViewById(R.id.search_image)
        val description : ImageView = itemView.findViewById(R.id.search_description)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
