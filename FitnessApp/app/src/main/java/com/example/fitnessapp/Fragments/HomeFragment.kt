package com.example.fitnessapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Activities.CreateWorkoutActivity
import com.example.fitnessapp.Activities.WorkoutPreviewActivity
import com.example.fitnessapp.Database.PremadeWorkouts
import com.example.fitnessapp.Database.PremadeWorkoutAdapter
import com.example.fitnessapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.grpc.InternalChannelz
import io.grpc.InternalChannelz.id

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutArrayList : ArrayList<PremadeWorkouts>
    private var idList = arrayListOf<String>()
    private var workoutId : String? = ""

    companion object {
        val refDb = Firebase.database.getReference("premadeWorkouts").orderByKey()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = (view.findViewById(R.id.premade_workouts)) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        workoutArrayList = arrayListOf<PremadeWorkouts>()

        recyclerView.visibility = View.GONE

        refDb.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutArrayList.clear()
                if (snapshot.exists()) {
                    for (workoutSnap in snapshot.children) {
                        val workoutData = workoutSnap.getValue(PremadeWorkouts::class.java)
                        workoutId = workoutSnap.key
                        workoutArrayList.add(workoutData!!)
                        idList.add(workoutId!!)
                    }
                    val adapter = PremadeWorkoutAdapter(workoutArrayList)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: PremadeWorkoutAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(activity, WorkoutPreviewActivity::class.java)
                            intent.putExtra("id", idList[position])
                            intent.putExtra("key", "premadeWorkouts")
                            startActivity(intent)
                        }
                    })
                }
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}