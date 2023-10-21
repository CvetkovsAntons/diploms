package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*
import java.util.concurrent.TimeUnit

class TimerActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var exercisesNumber: String
    private lateinit var exercise : String
    private lateinit var timer: CountDownTimer
    private lateinit var sDuration: String
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var timeLeft : Long = 0

    private lateinit var workoutId: String
    private lateinit var sectionKey: String
    private var historyId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        workoutId = intent.getStringExtra("id").toString()
        sectionKey = intent.getStringExtra("key").toString()
        exercisesNumber = intent.getStringExtra("exercise").toString()
        exercise = intent.getStringExtra("list").toString()
        historyId = intent.getIntExtra("history", 0)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        textView = findViewById(R.id.timer)

        val duration = TimeUnit.MINUTES.toMillis(1)

        timer_skip.setOnClickListener {
            newActivity()
        }

        timer_add.setOnClickListener {
            timer.cancel()
            timeLeft += 5000
            startTimer(timeLeft)
        }

        startTimer(duration)

        getName()
        getNextExercise()

    }

    private fun startTimer(duration: Long) {
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

                timeLeft = millisUntilFinished

                textView.text = sDuration
            }

            override fun onFinish() {
                newActivity()
            }
        }.start()
    }

    private fun newActivity() {
        var num = exercisesNumber.toInt()
        num += 1

        timer.cancel()

        val intent = Intent(this@TimerActivity, WorkoutActivity::class.java)
        intent.putExtra("exercise", num.toString())
        intent.putExtra("id", workoutId)
        intent.putExtra("key", sectionKey)
        intent.putExtra("history", historyId)
        startActivity(intent)
        finish()
    }

    private fun getName() {
        if (sectionKey == "premadeWorkouts") {
            database.getReference(sectionKey).child(workoutId).get().addOnSuccessListener {
                val name = it.child("name").value
                Log.v("ABOBA", exercise)

                timer_name.text = name.toString()
            }
        } else {
            database.getReference(sectionKey).child(auth.currentUser!!.uid).child("workouts").child(workoutId).get().addOnSuccessListener {
                val name = it.child("name").value

                timer_name.text = name.toString()
            }
        }

        var num = exercisesNumber.toInt()
        num += 2

        database.getReference("workoutExercises").child(auth.currentUser!!.uid)
            .child(sectionKey).child(workoutId).get().addOnSuccessListener {
                val count = num.toString() + "/" + it.childrenCount

                timer_count.text = count
            }
    }

    private fun getNextExercise() {
        database.getReference("exercises").child(exercise).get().addOnSuccessListener {
            val name = it.child("name").value.toString()

            timer_next.text = name
            Glide.with(getApplicationContext()).load(it.child("image_1").value).circleCrop().skipMemoryCache(true).into(findViewById(R.id.timer_image))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
    }
}