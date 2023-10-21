package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.fitnessapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_exercises_description.*

class ExercisesDescriptionActivity : AppCompatActivity() {

    private lateinit var database : FirebaseDatabase
    private lateinit var workout : String
    private lateinit var name : String
    private lateinit var edit : String
    private lateinit var check : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises_description)

        database = FirebaseDatabase.getInstance()

        val workoutId = intent.getStringExtra("id").toString()
        workout = intent.getStringExtra("workout").toString()
        name = intent.getStringExtra("name").toString()
        edit = intent.getStringExtra("edit").toString()
        check = intent.getStringExtra("check").toString()

        Log.v("drage", workoutId)

        getExerciseDescription(workoutId)
    }

    private fun getExerciseDescription(id: String) {
        database.getReference("exercises").child(id).get().addOnSuccessListener {
            val name = it.child("name").value
            val muscles = it.child("muscles").value
            val equipment = it.child("equipment").value
            val description = it.child("description").value

            exercise_description_name.text = name.toString()
            exercise_description_muscles.text = muscles.toString()
            exercise_description_equipment.text = equipment.toString()
            exercise_description.text = description.toString()

            Glide.with(this@ExercisesDescriptionActivity)
                .load(it.child("image_full").value).skipMemoryCache(true)
                .into(exercise_description_image)
        }
    }

    override fun onBackPressed() {
        if (check != "null") {
            val intent = Intent(this, SearchExercisesActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("workout", workout)
            intent.putExtra("edit", edit)
            startActivity(intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}