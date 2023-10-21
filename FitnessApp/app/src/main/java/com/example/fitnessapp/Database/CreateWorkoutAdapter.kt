package com.example.fitnessapp.Database

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Activities.WorkoutPreviewActivity
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import kotlin.coroutines.coroutineContext

class CreateWorkoutAdapter(private val exercisesPreviewList : ArrayList<ExerciseList>, private val workout : String, private val exerciseList: ArrayList<String>) : RecyclerView.Adapter<CreateWorkoutAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener
    private var database = FirebaseDatabase.getInstance()
    private var auth = FirebaseAuth.getInstance()

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.create_exercise, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val preview : ExerciseList = exercisesPreviewList[position]
        holder.name.text = preview.name
        holder.muscles.text = preview.muscles
        holder.equipment.text = preview.equipment
        Glide.with(holder.itemView.context).load(preview.image_1).circleCrop().skipMemoryCache(true).into(holder.image)

        holder.delete.setOnClickListener {
            database.getReference("workoutExercises")
                .child(auth.currentUser!!.uid)
                .child("usersWorkouts")
                .child(workout)
                .child(exerciseList[position])
                .removeValue()
        }
    }

    override fun getItemCount(): Int {
        return exercisesPreviewList.size
    }

    inner class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.create_name)
        var equipment : TextView = itemView.findViewById(R.id.create_equipment)
        var muscles : TextView = itemView.findViewById(R.id.create_muscles)
        var image : ImageView = itemView.findViewById(R.id.create_image)
        var delete : ImageView = itemView.findViewById(R.id.create_delete)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}