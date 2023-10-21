package com.example.fitnessapp.Database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.R

class WorkoutFinishAdapter(private val exercisesList : ArrayList<ExerciseList>) : RecyclerView.Adapter<WorkoutFinishAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutFinishAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout_finished_exercises, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: WorkoutFinishAdapter.ViewHolder, position: Int) {
        val exercises : ExerciseList = exercisesList[position]
        holder.name.text = exercises.name
        holder.equipment.text = exercises.equipment
        holder.equipment.text = exercises.muscles
        Glide.with(holder.itemView.context).load(exercises.image_1).circleCrop().skipMemoryCache(true).into(holder.image)
    }

    override fun getItemCount(): Int {
        return exercisesList.size
    }

    inner class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.finished_name)
        var equipment : TextView = itemView.findViewById(R.id.finished_equipment)
        var muscles : TextView = itemView.findViewById(R.id.finished_muscles)
        var image : ImageView = itemView.findViewById(R.id.finished_image)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}
