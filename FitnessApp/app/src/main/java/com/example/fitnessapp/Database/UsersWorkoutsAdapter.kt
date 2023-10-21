package com.example.fitnessapp.Database

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Activities.CreateWorkoutActivity
import com.example.fitnessapp.Activities.ExercisesDescriptionActivity
import com.example.fitnessapp.Activities.MainActivity
import com.example.fitnessapp.Fragments.WorkoutsFragment
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_workout.view.*
import kotlinx.android.synthetic.main.fragment_workouts.*
import kotlin.coroutines.coroutineContext

class UsersWorkoutsAdapter(private val usersWorkoutList : ArrayList<UsersWorkouts>, private val context : Context, private val fragment: WorkoutsFragment) : RecyclerView.Adapter<UsersWorkoutsAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener
    private var database = FirebaseDatabase.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private lateinit var builder : AlertDialog.Builder

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersWorkoutsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.users_workouts, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: UsersWorkoutsAdapter.ViewHolder, position: Int) {
        val workout : UsersWorkouts = usersWorkoutList[position]
        holder.name.text = workout.name
        holder.exercises.text = workout.exercises.toString()

        holder.edit.setOnClickListener {
            val workoutPosition = position + 1
            database.getReference("usersWorkouts")
                .child(auth.currentUser!!.uid)
                .child("workouts")
                .child("usersWorkouts_$workoutPosition")
                .get()
                .addOnSuccessListener {
                    val workoutId = it.key.toString()
                    val name = it.child("name").value.toString()

                    val intent = Intent(context, CreateWorkoutActivity::class.java)
                    intent.putExtra("edit", "edit")
                    intent.putExtra("name", name)
                    intent.putExtra("workout", workoutId)
                    context.startActivity(intent)
                }
        }

        holder.delete.setOnClickListener {
            val workoutPosition = position + 1

            builder = AlertDialog.Builder(context)
            builder.setTitle("Are You Sure?")
                .setMessage("Are you sure you want to delete this workout?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener {
                            dialog, id -> deleteWorkouts(workoutPosition, workout.id.toString())
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                .show()
        }
    }

    override fun getItemCount(): Int {
        return usersWorkoutList.size
    }

    inner class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.users_workouts_name)
        var exercises: TextView = itemView.findViewById(R.id.users_workouts_exercises_count)
        var edit : ImageView = itemView.findViewById(R.id.edit)
        var delete : ImageView = itemView.findViewById(R.id.delete)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    private fun deleteWorkouts(workoutPosition: Int, workout: String) {
        database.getReference("usersWorkouts")
            .child(auth.currentUser!!.uid)
            .child("workouts")
            .child(workout)
            .removeValue()

        database.getReference("workoutExercises")
            .child(auth.currentUser!!.uid)
            .child("usersWorkouts")
            .child(workout)
            .removeValue()

        database.getReference("users")
            .child(auth.currentUser!!.uid)
            .child("created")
            .get()
            .addOnSuccessListener {
                val count = it.value.toString()
                var countOfWorkouts = count.toInt()
                countOfWorkouts -= 1

                database.getReference("users")
                    .child(auth.currentUser!!.uid)
                    .child("created")
                    .setValue(countOfWorkouts)
            }
    }
}