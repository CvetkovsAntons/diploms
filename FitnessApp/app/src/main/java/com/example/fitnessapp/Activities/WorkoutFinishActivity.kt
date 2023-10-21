package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Database.ExerciseList
import com.example.fitnessapp.Database.WorkoutFinishAdapter
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_workout_finish.*

class WorkoutFinishActivity : AppCompatActivity() {

    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth

    private lateinit var workoutId: String
    private lateinit var sectionKey: String
    private var historyId = 0
    private lateinit var finishedWorkouts : String

    private lateinit var recyclerView: RecyclerView

    private lateinit var exercisesList : ArrayList<String>
    private lateinit var exercisesArrayList : ArrayList<ExerciseList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_finish)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        workoutId = intent.getStringExtra("id").toString()
        sectionKey = intent.getStringExtra("key").toString()
        historyId = intent.getIntExtra("history", 0)

        recyclerView = findViewById<RecyclerView>(R.id.workout_finished_exercises_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        exercisesList = arrayListOf<String>()
        exercisesArrayList = arrayListOf<ExerciseList>()

        workout_finished_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragment", "homePage")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        getFinishedWorkouts()
        getWorkoutInfo()
        getExercisesList()
    }

    private fun getWorkoutInfo() {
        if (sectionKey == "premadeWorkouts") {
            database.getReference(sectionKey)
                .child(workoutId)
                .get()
                .addOnSuccessListener {
                val name = it.child("name").value.toString()

                workout_finished_name.text = name
            }
        } else {
            database.getReference(sectionKey)
                .child(auth.currentUser!!.uid)
                .child("workouts")
                .child(workoutId)
                .get()
                .addOnSuccessListener {
                val name = it.child("name").value.toString()

                workout_finished_name.text = name
            }
        }

        database.getReference("workoutExercises")
            .child(auth.currentUser!!.uid)
            .child(sectionKey)
            .child(workoutId)
            .get()
            .addOnSuccessListener {
                val exercises = it.childrenCount.toString()

                workout_finished_exercises.text = exercises
            }
    }

    private fun getFinishedWorkouts() {
        database.getReference("users").child(auth.currentUser!!.uid).get().addOnSuccessListener {
            finishedWorkouts = it.child("finished").value.toString()
            val workouts = finishedWorkouts.toInt()

            workout_finished.text = (workouts + 1).toString()

            setStatus()
        }
    }

    private fun getExercisesList() {
        database.getReference("history").child(auth.currentUser!!.uid)
            .child("history_$historyId").child("exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exercisesList.clear()
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val snapData = snap.key.toString()
                            exercisesList.add(snapData)
                        }
                        getExercises()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getExercises() {
        database.getReference("exercises")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exercisesArrayList.clear()
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            for (i in exercisesList) {
                                if (snap.key == i) {
                                    val snapData = snap.getValue(ExerciseList::class.java)
                                    exercisesArrayList.add(snapData!!)
                                }
                            }
                        }
                        val adapter = WorkoutFinishAdapter(exercisesArrayList)
                        recyclerView.adapter = adapter
                        adapter.setOnItemClickListener(object : WorkoutFinishAdapter.onItemClickListener {
                            override fun onItemClick(position: Int) {
                                val intent = Intent(this@WorkoutFinishActivity, ExercisesDescriptionActivity::class.java)
                                intent.putExtra("id", exercisesList[position])
                                startActivity(intent)
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun setStatus() {
        val workouts = finishedWorkouts.toInt() + 1
        database.getReference("users").child(auth.currentUser!!.uid)
            .child("finished").setValue(workouts)
        database.getReference("history").child(auth.currentUser!!.uid)
            .child("history_" + historyId).child("status").setValue("finished")
    }

    override fun onBackPressed() {
        val workouts = finishedWorkouts.toInt()
        database.getReference("users").child(auth.currentUser!!.uid)
            .child("finished").setValue(workouts)
        database.getReference("history").child(auth.currentUser!!.uid)
            .child("history_" + historyId).child("status").setValue("unfinished")
        super.onBackPressed()
    }
}