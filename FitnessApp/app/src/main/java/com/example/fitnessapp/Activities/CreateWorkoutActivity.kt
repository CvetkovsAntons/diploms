package com.example.fitnessapp.Activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Database.*
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_workout.*

class CreateWorkoutActivity : AppCompatActivity() {

    private lateinit var builder : AlertDialog.Builder
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var usersWorkoutsList : ArrayList<ExerciseList>
    private lateinit var exerciseList: ArrayList<String>
    private lateinit var name : String
    private lateinit var edit : String

    private lateinit var workout : String
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_workout)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        workout = intent.getStringExtra("workout").toString()
        name = intent.getStringExtra("name").toString()
        edit = intent.getStringExtra("edit").toString()
//        exercise = intent.getStringExtra("exercise").toString()

        if (name == "null") {
            workout_name.setText("")
        } else {
            workout_name.setText(name)
        }

        if (edit == "edit") {
            header.setText("Workout Edit")
        }


        builder = AlertDialog.Builder(this)

        recyclerView = findViewById(R.id.exercise_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        usersWorkoutsList = arrayListOf<ExerciseList>()
        exerciseList = arrayListOf<String>()

        add_exercises_button.setOnClickListener {
            val intent = (Intent(this, SearchExercisesActivity::class.java))
            intent.putExtra("workout", workout)
            intent.putExtra("name", workout_name.text.toString())
            intent.putExtra("edit", edit)
            startActivity(intent)
            finish()
        }

        save_workout_button.setOnClickListener {
            saveWorkoutData()
        }

        workout_name.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                name = workout_name.text.toString()
                false
            } else {
                false
            }
        }

        getExercises()
    }

    private fun getExercises() {
        database.getReference("workoutExercises")
            .child(auth.currentUser!!.uid)
            .child("usersWorkouts")
            .child(workout)
            .orderByChild("position")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exerciseList.clear()
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            exerciseList.add(snap.key.toString())
                        }
                    }
                    if (exerciseList.size == 0) {
                        exercise_list.visibility = View.GONE
                    } else {
                        showExercises()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun showExercises() {
        usersWorkoutsList.clear()
        for (position in exerciseList) {
            database.getReference("exercises")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (snap in snapshot.children) {
                                if (snap.key == position) {
                                    val exerciseData = snap.getValue(ExerciseList::class.java)
                                    usersWorkoutsList.add(exerciseData!!)
                                }
                            }
                            getAdapter()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun getAdapter() {
        val adapter = CreateWorkoutAdapter(usersWorkoutsList, workout, exerciseList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : CreateWorkoutAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@CreateWorkoutActivity, ExercisesDescriptionActivity::class.java)
                intent.putExtra("id", exerciseList[position])
                startActivity(intent)
            }
        })
    }

    private fun saveWorkoutData() {
        var countOfSets = 0

        database.getReference("usersWorkouts")
            .child(auth.currentUser!!.uid)
            .child("workouts")
            .get()
            .addOnSuccessListener {
                val childrenCount = it.childrenCount.toString()
                count = childrenCount.toInt()

                if (name == "" || name == "null") {
                    name = "WORKOUT $count"
                } else {
                    name = workout_name.text.toString()
                }

                database.getReference("usersWorkouts")
                    .child(auth.currentUser!!.uid)
                    .child("workouts")
                    .child(workout)
                    .child("name")
                    .setValue(name)
        }

        database.getReference("workoutExercises")
            .child(auth.currentUser!!.uid)
            .child("usersWorkouts")
            .child(workout)
            .get()
            .addOnSuccessListener {
                countOfSets = it.childrenCount.toInt()

                if (countOfSets == 0) {
                    Toast.makeText(this, "ADD SOME EXERCISES", Toast.LENGTH_SHORT).show()
                } else {
                    database.getReference("usersWorkouts")
                        .child(auth.currentUser!!.uid)
                        .child("workouts")
                        .child(workout)
                        .child("exercises")
                        .setValue(countOfSets)

                    finish()
                }
            }
    }

    override fun onBackPressed() {
        if (edit != "edit") {
            builder.setTitle("Are You Sure?")
                .setMessage("Are you sure you want to cancel workout creation?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener {
                            dialog, id -> deleteWorkout()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                .show()
        } else {
            database.getReference("workoutExercises")
                .child(auth.currentUser!!.uid)
                .child("usersWorkouts")
                .child(workout)
                .get()
                .addOnSuccessListener {
                    if (it.value != null) {
                        saveWorkoutData()
                        super.onBackPressed()
                    } else {
                        Toast.makeText(this, "ADD SOME EXERCISES", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun deleteWorkout() {
        database.getReference("users")
            .child(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                var countCreated = it.child("created").value.toString().toInt()
                countCreated -= 1

                database.getReference("users")
                    .child(auth.currentUser!!.uid)
                    .child("created")
                    .setValue(countCreated)
        }
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
        finish()
    }
}