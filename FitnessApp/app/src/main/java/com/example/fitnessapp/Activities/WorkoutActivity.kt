package com.example.fitnessapp.Activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Database.*
import com.example.fitnessapp.Fragments.TimerDialogFragment
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_workout.*
import layout.WorkoutExercisesInfoAdapter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class WorkoutActivity : AppCompatActivity() {


    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView

    private lateinit var setsArrayList : ArrayList<WorkoutExercisesSets>
    private lateinit var exercisesList : ArrayList<String>
    private lateinit var builder : AlertDialog.Builder

    private lateinit var workoutId: String
    private lateinit var sectionKey: String
    private lateinit var exercisesNumber: String
    private lateinit var equipmentSet : String
    private lateinit var name : String
    private var setsCount = 0
    private var count = 0
    private var historyId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        workoutId = intent.getStringExtra("id").toString()
        sectionKey = intent.getStringExtra("key").toString()
        exercisesNumber = intent.getStringExtra("exercise").toString()
        historyId = intent.getIntExtra("history", 0)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        builder = AlertDialog.Builder(this)

        recyclerView = findViewById(R.id.workout_exercises_sets)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setsArrayList = arrayListOf<WorkoutExercisesSets>()
        exercisesList = arrayListOf<String>()

        val num = exercisesNumber.toInt()

        workout_exercises_button_skip.setOnClickListener {
            if (num == exercisesList.size - 1) {
                val intent = Intent(this, WorkoutFinishActivity::class.java)
                intent.putExtra("id", workoutId)
                intent.putExtra("key", sectionKey)
                intent.putExtra("history", historyId)
                startActivity(intent)
            } else {
                newActivitySkip()
            }

        }

        workout_button_add.setOnClickListener {
            addWorkoutSets(sectionKey, workoutId, exercisesList[num])
        }

        workout_stop_button.setOnClickListener {
            alertDialog()
        }

        workout_exercises_button.setOnClickListener {
            val sets = setsArrayList[count].reps
            if (sets == null || sets == 0.toLong()) {
                Toast.makeText(this, "DO SOME REPS!!!", Toast.LENGTH_SHORT).show()
            } else {
                count += 1

                if (num == exercisesList.size - 1 && count == setsCount) {
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("exercises")
                        .child(exercisesList[exercisesNumber.toInt()]).child("position").setValue(exercisesNumber.toInt() + 1)

                    val intent = Intent(this, WorkoutFinishActivity::class.java)
                    intent.putExtra("id", workoutId)
                    intent.putExtra("key", sectionKey)
                    intent.putExtra("history", historyId)
                    startActivity(intent)
                } else if (count < setsCount) {
                    when (count) {
                        setsCount - 1 -> workout_exercises_button.text = "NEXT EXERCISE"
                    }
                    getTimer()
                } else {
                    newActivity()
                }
            }
        }

        getExercisesList(sectionKey, workoutId)
        when (exercisesNumber.toInt()) {
            0 -> getHistoryId()
        }

    }

    private fun getExercisesList(key: String, id: String) {
        val num = exercisesNumber.toInt()
        database.getReference("workoutExercises").child(auth.currentUser!!.uid).child(key).child(id)
            .orderByChild("position")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    exercisesList.clear()
                    if (snapshot.exists()) {
                        for (exercises in snapshot.children) {
                            exercisesList.add(exercises.key.toString())
                        }

                        getWorkoutDescription(exercisesList[num])
                        getWorkout(key, id, exercisesList[num])
                        getWorkoutSets(key, id, exercisesList[num])
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getWorkoutDescription(exercise: String) {
        workout_exercises_description.setOnClickListener {
            database.getReference("exercises").child(exercise).get().addOnSuccessListener {
                builder.setTitle("Exercise description")
                    .setMessage(it.child("description").value.toString())
                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                        })
                    .show()

            }
        }
    }

    private fun getWorkout(key: String, id: String, exercise: String) {
        if (key == "premadeWorkouts") {
            database.getReference(key).child(id).get().addOnSuccessListener {
                name = it.child("name").value.toString()

                var countExercises = exercisesNumber.toInt()
                countExercises += 1

                workout_name.text = name
                workout_exercises_count.text =  countExercises.toString() + "/" + exercisesList.size
            }
        } else {
            database.getReference(key).child(auth.currentUser!!.uid).child("workouts")
                .child(id).get().addOnSuccessListener {
                    name = it.child("name").value.toString()

                        Log.v("drage", name)

                    var countExercises = exercisesNumber.toInt()
                    countExercises += 1

                    workout_name.text = name.toString()
                    workout_exercises_count.text =  countExercises.toString() + "/" + exercisesList.size
            }
        }

        database.getReference("exercises").child(exercise).get().addOnSuccessListener {
            val exercises = it.child("name").value
            Glide.with(getApplicationContext()).load(it.child("image_full").value).skipMemoryCache(true).into(findViewById(R.id.workout_exercises_image))

            workout_exercises.text = exercises.toString()
        }
    }

    private fun getHistoryId() {
        if (sectionKey == "premadeWorkouts") {
            database.getReference(sectionKey).child(workoutId).get().addOnSuccessListener {
                val name = it.child("name").value.toString()

                workout_name.text = name

                database.getReference("history").child(auth.currentUser!!.uid).get().addOnSuccessListener {
                    historyId = it.childrenCount.toInt() + 1
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("date").setValue(System.currentTimeMillis())
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("workout").setValue(name)
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("status").setValue("unfinished")

                }.addOnFailureListener {
                    historyId = 1
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("date").setValue(System.currentTimeMillis())
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("workout").setValue(name)
                    database.getReference("history").child(auth.currentUser!!.uid)
                        .child("history_" + historyId).child("status").setValue("unfinished")
                }
            }
        } else {
            database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("workouts")
                .child(workoutId).get().addOnSuccessListener {
                    val name = it.child("name").value

                    workout_name.text = name.toString()

                    database.getReference("history").child(auth.currentUser!!.uid).get().addOnSuccessListener {
                        historyId = it.childrenCount.toInt() + 1
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("date").setValue(System.currentTimeMillis())
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("workout").setValue(name)
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("status").setValue("unfinished")

                    }.addOnFailureListener {
                        historyId = 1
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("date").setValue(System.currentTimeMillis())
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("workout").setValue(name)
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("status").setValue("unfinished")
                    }
                }
        }
    }

    private fun getWorkoutSets(key: String, id: String, exercise: String) {
        database.getReference("workoutExercises").child(auth.currentUser!!.uid)
            .child(key).child(id).child(exercise).child("sets").orderByChild("position")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    setsArrayList.clear()
                    setsCount = 0
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val snapData = snap.getValue(WorkoutExercisesSets::class.java)
                            setsCount = snapshot.childrenCount.toInt()
                            setsArrayList.add(snapData!!)
                        }
                        getAdapter()
                    } else {
                        return
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getAdapter() {
        val num = exercisesNumber.toInt()

        database.getReference("exercises").child(exercisesList[num]).get().addOnSuccessListener {
            equipmentSet = it.child("equipment").value.toString()

            if (equipmentSet == "Body Only" || equipmentSet == "Bench") {
                val adapter = WorkoutExercisesSetsBodyAdapter(setsArrayList, workoutId, sectionKey, exercisesList[num], this)
                recyclerView.adapter = adapter
            } else {
                val adapter = WorkoutExercisesSetsAdapter(setsArrayList, workoutId, sectionKey, exercisesList[num], this)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun addWorkoutSets(key: String, id: String, exercise: String) {
        val snapshot = database.getReference("workoutExercises").child(auth.currentUser!!.uid)
            .child(key).child(id).child(exercise).child("sets")
        val countOfSets = setsCount + 1
        snapshot.child("set_" + countOfSets).child("position").setValue(countOfSets)
    }

    private fun getTimer() {
        val dialog = TimerDialogFragment()

        dialog.show(supportFragmentManager, "customDialog")
    }


    private fun newActivity() {
        database.getReference("history").child(auth.currentUser!!.uid)
            .child("history_" + historyId).child("exercises")
            .child(exercisesList[exercisesNumber.toInt()]).child("position").setValue(exercisesNumber.toInt() + 1)

        var num = exercisesNumber.toInt()
        num += 1

        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra("exercise", exercisesNumber)
        intent.putExtra("id", workoutId)
        intent.putExtra("key", sectionKey)
        intent.putExtra("list", exercisesList[num])
        intent.putExtra("history", historyId)
        startActivity(intent)
    }

    private fun newActivitySkip() {
        var num = exercisesNumber.toInt()
        num += 1

        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra("exercise", exercisesNumber)
        intent.putExtra("id", workoutId)
        intent.putExtra("key", sectionKey)
        intent.putExtra("list", exercisesList[num])
        intent.putExtra("history", historyId)
        Log.v("OPEN THE DOOR", historyId.toString())
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (exercisesNumber.toInt() == 0) {
            alertDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun alertDialog() {
        builder.setTitle("Are You Sure?")
            .setMessage("Are you sure you want to quit workout?")
            .setCancelable(true)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener {
                        dialog, id -> stop()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            .show()
    }

    private fun stop() {
        if (sectionKey == "premadeWorkouts") {
            database.getReference(sectionKey).child(workoutId).get().addOnSuccessListener {
                database.getReference("history").child(auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        historyId = it.childrenCount.toInt()
                        database.getReference("history").child(auth.currentUser!!.uid)
                            .child("history_" + historyId).child("status").setValue("cancelled")
                    }
            }
        } else {
            database.getReference(sectionKey).child(auth.currentUser!!.uid)
                .child(workoutId).get().addOnSuccessListener {
                    database.getReference("history").child(auth.currentUser!!.uid).get()
                        .addOnSuccessListener {
                            historyId = it.childrenCount.toInt()
                            database.getReference("history").child(auth.currentUser!!.uid)
                                .child("history_" + historyId).child("status").setValue("cancelled")
                        }
                }
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", "homePage")
        startActivity(intent)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
    }
}

