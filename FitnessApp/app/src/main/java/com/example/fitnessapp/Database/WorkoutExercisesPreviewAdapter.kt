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
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import kotlin.coroutines.coroutineContext

class WorkoutExercisesPreviewAdapter(private val exercisesPreviewList : ArrayList<WorkoutExercisesPreview>, private val setsList : ArrayList<String>) : RecyclerView.Adapter<WorkoutExercisesPreviewAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exercices_preview, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val preview : WorkoutExercisesPreview = exercisesPreviewList[position]
        val sets : String = setsList[position]
        holder.name.text = preview.name
        holder.equipment.text = preview.equipment
        holder.sets.text = sets
        Glide.with(holder.itemView.context).load(preview.image_1).circleCrop().skipMemoryCache(true).into(holder.image)
    }

    override fun getItemCount(): Int {
        return exercisesPreviewList.size
    }

    inner class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.preview_name)
        var equipment : TextView = itemView.findViewById(R.id.preview_equipment)
        var sets : TextView = itemView.findViewById(R.id.preview_sets)
        var image : ImageView = itemView.findViewById(R.id.preview_image)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}