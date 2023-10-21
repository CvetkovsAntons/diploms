package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Database.PremadeWorkouts
import com.example.fitnessapp.Database.WorkoutExercisesPreview
import com.example.fitnessapp.Database.WorkoutExercisesPreviewAdapter
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_workout_preview.*
import kotlinx.android.synthetic.main.exercices_preview.*
import java.lang.reflect.Array.set

class WorkoutPreviewActivity : AppCompatActivity() {



    private lateinit var database: FirebaseDatabase
    private lateinit var auth : FirebaseAuth



    private lateinit var workoutId: String
    private lateinit var sectionKey: String
    private lateinit var startedWorkout : String
    private lateinit var name : String



    private lateinit var recyclerView: RecyclerView
    private lateinit var exercisesPreviewList: ArrayList<WorkoutExercisesPreview>



    private var exercisesList = arrayListOf<String>()
    private var setsCountList = arrayListOf<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_preview)

        workoutId = intent.getStringExtra("id").toString()
        sectionKey = intent.getStringExtra("key").toString()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById<RecyclerView>(R.id.preview_exercises_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        exercisesPreviewList = arrayListOf<WorkoutExercisesPreview>()

        preview_start_button.setOnClickListener {
            var started = startedWorkout.toInt()
            started += 1
            database.getReference("users").child(auth.currentUser!!.uid).child("started").setValue(started)

            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("id", workoutId)
            intent.putExtra("key", sectionKey)
            intent.putExtra("exercise", "0")
            startActivity(intent)
            finish()
        }

        if (sectionKey == "usersWorkouts") {
            preview_muscles.visibility = View.GONE
        }

        getWorkoutName(sectionKey, workoutId)
        getExercisesId(sectionKey, workoutId)

        getStartedWorkout()
    }

    private fun getStartedWorkout() {
        database.getReference("users").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            startedWorkout = it.child("started").value.toString()
        }
    }

    private fun getWorkoutName(key: String, id: String) {
        if (key == "premadeWorkouts") {
            database.getReference(key)
                .child(id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        name = snapshot.child("name").value.toString()
                        val muscles = snapshot.child("muscles").value
                        val exercises = snapshot.child("exercises").value

                        preview_workout_name.text = name
                        preview_workout_muscles.text = muscles.toString()
                        preview_workout_exercises.text = exercises.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        } else {
            database.getReference(key)
                .child(auth.currentUser!!.uid)
                .child("workouts")
                .child(id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        name = snapshot.child("name").value.toString()
                        val exercises = snapshot.child("exercises").value

                        preview_workout_name.text = name
                        preview_workout_exercises.text = exercises.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun getExercisesId(key: String, id: String) {
        database.getReference("workoutExercises").child(auth.currentUser!!.uid)
            .child(key).child(id).orderByChild("position").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exercisesList.clear()
                    setsCountList.clear()
                    if (snapshot.exists()) {
                        for (info in snapshot.children) {
                            val name = info.key
                            val setsCount = info.child("sets").childrenCount
                            setsCountList.add(setsCount.toString())
                            exercisesList.add(name.toString())
                        }
                        getExercisesInfo()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getExercisesInfo() {
        exercisesPreviewList.clear()
        for (position in exercisesList) {
            database.getReference("exercises").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (infoSnap in snapshot.children) {
                            if (infoSnap.key == position) {
                                val exercisesData = infoSnap.getValue(WorkoutExercisesPreview::class.java)
                                exercisesPreviewList.add(exercisesData!!)
                            }
                        }
                        getAdapter()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }



    private fun getAdapter() {
        val adapter = WorkoutExercisesPreviewAdapter(exercisesPreviewList, setsCountList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : WorkoutExercisesPreviewAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@WorkoutPreviewActivity, ExercisesDescriptionActivity::class.java)
                intent.putExtra("id", exercisesList[position])
                startActivity(intent)
            }
        })
    }
}