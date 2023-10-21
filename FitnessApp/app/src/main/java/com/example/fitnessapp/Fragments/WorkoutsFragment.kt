package com.example.fitnessapp.Fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Activities.*
import com.example.fitnessapp.Database.*
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_results.*
import kotlinx.android.synthetic.main.fragment_workouts.*

class WorkoutsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private var count : Long = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var usersWorkoutsList : ArrayList<UsersWorkouts>
    private lateinit var workoutId : String
    private lateinit var idList : ArrayList<String>
    private lateinit var builder : AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workouts, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        builder = AlertDialog.Builder(this.requireContext())

        no_workouts_created.visibility = View.GONE
        users_workouts_delete.visibility = View.GONE

        recyclerView = (view.findViewById(R.id.users_workouts_list)) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        usersWorkoutsList = arrayListOf<UsersWorkouts>()
        idList = arrayListOf<String>()

        create_workout_button.setOnClickListener {
            createWorkout()
        }

        users_workouts_delete.setOnClickListener {
            alertDialog()
        }

        getWorkout()
    }

    private fun getWorkout() {
        database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("workouts")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    usersWorkoutsList.clear()
                    if (snapshot.exists()) {
                        for (workoutSnap in snapshot.children) {
                            val workoutData = workoutSnap.getValue(UsersWorkouts::class.java)
                            workoutId = workoutSnap.key.toString()
                            usersWorkoutsList.add(workoutData!!)
                            idList.add(workoutId)
                        }
                        adapter()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        database.getReference("users").child(auth.currentUser!!.uid).child("created")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    visibility()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun  createWorkout() {
        val snapshot = database.getReference("usersWorkouts").child(auth.currentUser!!.uid)
        var id = 0

        snapshot.child("created").get().addOnSuccessListener {
            id = it.value.toString().toInt()
            id += 1
        }.addOnFailureListener {
                Log.v("error", "error")
            }

        snapshot.get().addOnSuccessListener {

            snapshot.child("workouts").child("usersWorkouts_$id").child("name").setValue("WORKOUT $id")
            snapshot.child("workouts").child("usersWorkouts_$id").child("id").setValue("usersWorkouts_$id")
            snapshot.child("workouts").child("usersWorkouts_$id").child("exercises").setValue(0)
            snapshot.child("created").setValue(id)
            val intent = Intent(this.context, CreateWorkoutActivity::class.java)
            intent.putExtra("workout", "usersWorkouts_$id")
            startActivity(intent)
        }
            .addOnFailureListener {
                snapshot.child("workouts").child("usersWorkouts_1").child("name").setValue("WORKOUT $id")
                snapshot.child("workouts").child("usersWorkouts_1").child("id").setValue("usersWorkouts_1")
                snapshot.child("workouts").child("usersWorkouts_1").child("exercises").setValue(0)
                snapshot.child("created").setValue(1)
                val intent = Intent(this.context, CreateWorkoutActivity::class.java)
                intent.putExtra("workout", "usersWorkouts_1")
                startActivity(intent)
            }

        database.getReference("users").child(auth.currentUser!!.uid).child("created").setValue(count + 1)
    }

    private fun visibility() {
        database.getReference("users").child(auth.currentUser!!.uid)
            .get().addOnSuccessListener {
                val c = it.child("created").value.toString()
                count = c.toLong()
                if (c.toLong() < 1.toLong()) {
                    users_workouts_list.visibility = View.GONE
                    users_workouts_delete.visibility = View.GONE
                    no_workouts_created.visibility = View.VISIBLE
                } else {
                    users_workouts_list.visibility = View.VISIBLE
                    users_workouts_delete.visibility = View.VISIBLE
                    no_workouts_created.visibility = View.GONE
                }
            }.addOnFailureListener {
                Log.v("error", "no workout")
            }
    }

    private fun alertDialog() {
        builder.setTitle("Are You Sure?")
            .setMessage("Are you sure you  want to delete all your workouts?")
            .setCancelable(true)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener {
                        dialog, id -> deleteWorkouts()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            .show()
    }

    private fun deleteWorkouts() {
        database.getReference("users").child(auth.currentUser!!.uid).child("created").setValue(0)
        database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("workouts").removeValue()
        database.getReference("usersWorkouts").child(auth.currentUser!!.uid).child("created").setValue(0)
        database.getReference("workoutExercises").child(auth.currentUser!!.uid).child("usersWorkouts").removeValue()
    }

    private fun adapter() {
        if (activity != null) {
            val adapter = UsersWorkoutsAdapter(usersWorkoutsList, requireActivity(), WorkoutsFragment())
            recyclerView.adapter = adapter
            adapter.setOnItemClickListener(object: UsersWorkoutsAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    val intent = Intent(activity, WorkoutPreviewActivity::class.java)
                    intent.putExtra("id", idList[position])
                    intent.putExtra("key", "usersWorkouts")
                    startActivity(intent)
                }
            })
        }
    }
}