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
import kotlinx.android.synthetic.main.activity_results_details.*
import java.text.SimpleDateFormat

class ResultsDetailsActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var exercisesList : ArrayList<String>
    private lateinit var exercisesArrayList : ArrayList<ExerciseList>

    private lateinit var recyclerView: RecyclerView

    private lateinit var workout : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_details)

        workout = intent.getStringExtra("workout").toString()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById<RecyclerView>(R.id.details_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        exercisesList = arrayListOf<String>()
        exercisesArrayList = arrayListOf<ExerciseList>()

        getData()
    }

    private fun getData() {
        database.getReference("history")
            .child(auth.currentUser!!.uid)
            .child(workout)
            .get()
            .addOnSuccessListener {
                val name = it.child("workout").value.toString()
                val status = it.child("status").value.toString()
                val d = it.child("date").value.toString()
                val dateFormat = SimpleDateFormat("dd/MM/yy")
                val timeFormat = SimpleDateFormat("hh:mm")
                val date = dateFormat.format(d.toLong())
                val time = timeFormat.format(d.toLong())

                details_workout.text = name
                details_date.text = date
                details_time.text = time
                details_status.text = status

                for (i in it.child("exercises").children) {
                    exercisesList.add(i.key.toString())
                }
            }

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
                                val intent = Intent(this@ResultsDetailsActivity, ExercisesDescriptionActivity::class.java)
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
}