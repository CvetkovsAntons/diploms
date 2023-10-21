package com.example.fitnessapp.Database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Fragments.HomeFragment
import com.example.fitnessapp.R

class PremadeWorkoutAdapter(private val premadeWorkoutList : ArrayList<PremadeWorkouts>) : RecyclerView.Adapter<PremadeWorkoutAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.premade_workouts, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout : PremadeWorkouts = premadeWorkoutList[position]
        holder.name.text = workout.name
        holder.exercises.text = workout.exercises
        holder.muscle.text = workout.muscles
        Glide.with(holder.itemView.context).load(workout.image).circleCrop().skipMemoryCache(true).into(holder.image)
    }

    override fun getItemCount(): Int {
        return premadeWorkoutList.size
    }

    inner class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.workout_name)
        var muscle : TextView = itemView.findViewById(R.id.workout_muscle)
        var exercises : TextView = itemView.findViewById(R.id.workout_exercises)
        var image : ImageView = itemView.findViewById(R.id.workout_image)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}