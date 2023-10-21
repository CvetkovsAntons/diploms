package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Database.*
import com.example.fitnessapp.Fragments.HomeFragment
import com.example.fitnessapp.R
import com.google.common.collect.Collections2.filter
import com.google.common.collect.Iterables.filter
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_workout.*
import kotlinx.android.synthetic.main.activity_search_exercises.*
import java.util.Locale.filter

class SearchExercisesActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var exercisesArrayList : ArrayList<WorkoutExercisesPreview>
    private var idList = arrayListOf<String>()
    private var workoutId : String? = ""
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth

    private lateinit var workout : String
    private lateinit var name : String
    private lateinit var edit : String
    private var countExercises = 1

    private var abs : Boolean = false
    private var chest : Boolean = false
    private var back : Boolean = false
    private var legs : Boolean = false
    private var biceps : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_exercises)

        workout = intent.getStringExtra("workout").toString()
        name = intent.getStringExtra("name").toString()
        edit = intent.getStringExtra("edit").toString()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = (findViewById(R.id.exercise_list)) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        exercisesArrayList = arrayListOf<WorkoutExercisesPreview>()

        search_abs.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                abs = true
                showExerciseList()
            } else {
                abs = false
                showExerciseList()
            }
        }

        search_chest.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                chest = true
                showExerciseList()
            } else {
                chest = false
                showExerciseList()
            }
        }

        search_chest.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                chest = true
                showExerciseList()
            } else {
                chest = false
                showExerciseList()
            }
        }

        search_back.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                back = true
                showExerciseList()
            } else {
                back = false
                showExerciseList()
            }
        }

        search_legs.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                legs = true
                showExerciseList()
            } else {
                legs = false
                showExerciseList()
            }
        }

        search_biceps.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                biceps = true
                showExerciseList()
            } else {
                biceps = false
                showExerciseList()
            }
        }

        countExercises()
        showExerciseList()
    }

    private fun countExercises() {
        database.getReference("workoutExercises").child(auth.currentUser!!.uid).child("usersWorkouts")
            .child(workout).get().addOnSuccessListener {
                countExercises = it.childrenCount.toInt()
                countExercises += 1
            }
    }

    private fun showExerciseList() {
        database.getReference("exercises").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exercisesArrayList.clear()
                idList.clear()
                if (snapshot.exists()) {
                    for (exerciseSnap in snapshot.children) {
                        if (abs) {
                            if (exerciseSnap.child("muscles").value.toString() == "Abs") {
                                val exerciseData = exerciseSnap.getValue(WorkoutExercisesPreview::class.java)
                                workoutId = exerciseSnap.key
                                exercisesArrayList.add(exerciseData!!)
                                idList.add(workoutId!!)
                            }
                        }
                        if (chest) {
                            if (exerciseSnap.child("muscles").value.toString() == "Chest") {
                                val exerciseData = exerciseSnap.getValue(WorkoutExercisesPreview::class.java)
                                workoutId = exerciseSnap.key
                                exercisesArrayList.add(exerciseData!!)
                                idList.add(workoutId!!)
                            }
                        }
                        if (back) {
                            if (exerciseSnap.child("muscles").value.toString() == "Back") {
                                val exerciseData = exerciseSnap.getValue(WorkoutExercisesPreview::class.java)
                                workoutId = exerciseSnap.key
                                exercisesArrayList.add(exerciseData!!)
                                idList.add(workoutId!!)
                            }
                        }
                        if (legs) {
                            if (exerciseSnap.child("muscles").value.toString() == "Legs") {
                                val exerciseData = exerciseSnap.getValue(WorkoutExercisesPreview::class.java)
                                workoutId = exerciseSnap.key
                                exercisesArrayList.add(exerciseData!!)
                                idList.add(workoutId!!)
                            }
                        }
                        if (biceps) {
                            if (exerciseSnap.child("muscles").value.toString() == "Biceps") {
                                val exerciseData = exerciseSnap.getValue(WorkoutExercisesPreview::class.java)
                                workoutId = exerciseSnap.key
                                exercisesArrayList.add(exerciseData!!)
                                idList.add(workoutId!!)
                            }
                        }

                        if (!abs && !chest && !back && !legs && !biceps) {
                            Log.v("drage", "true")
                            val exerciseData = exerciseSnap.getValue(WorkoutExercisesPreview::class.java)
                            workoutId = exerciseSnap.key
                            exercisesArrayList.add(exerciseData!!)
                            idList.add(workoutId!!)
                        }
                    }
                    val adapter = SearchAdapter(exercisesArrayList, this@SearchExercisesActivity, idList, name, workout, edit)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: SearchAdapter.onItemClickListener{
                       override fun onItemClick(position: Int) {
                           Log.v("drage", idList.toString())
                           database.getReference("workoutExercises").child(auth.currentUser!!.uid).child("usersWorkouts")
                               .child(workout).child(idList[position]).child("sets").child("set_1")
                               .child("position").setValue(1)
                           database.getReference("workoutExercises").child(auth.currentUser!!.uid).child("usersWorkouts")
                               .child(workout).child(idList[position]).child("position").setValue(countExercises)
                           val intent = Intent(this@SearchExercisesActivity, CreateWorkoutActivity::class.java)
                           intent.putExtra("workout", workout)
                           intent.putExtra("name", name)
                           intent.putExtra("edit", edit)
                           startActivity(intent)
                           finish()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, CreateWorkoutActivity::class.java)
        intent.putExtra("workout", workout)
        intent.putExtra("name", name)
        intent.putExtra("edit", edit)
        startActivity(intent)
        finish()
    }
}
