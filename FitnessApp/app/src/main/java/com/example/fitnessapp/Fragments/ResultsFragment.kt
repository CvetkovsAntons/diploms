package com.example.fitnessapp.Fragments

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
import com.example.fitnessapp.Activities.CreateWorkoutActivity
import com.example.fitnessapp.Activities.ResultsDetailsActivity
import com.example.fitnessapp.Database.ResultsAdapter
import com.example.fitnessapp.Database.SearchAdapter
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_results.*
import java.text.SimpleDateFormat

class ResultsFragment : Fragment() {

    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var nameList : ArrayList<String>
    private lateinit var dateList : ArrayList<String>
    private lateinit var timeList : ArrayList<String>
    private lateinit var statusList: ArrayList<String>
    private lateinit var idList: ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var builder : AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_results, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        results_text.visibility = View.GONE
        results_delete.visibility = View.GONE

        builder = AlertDialog.Builder(this.requireContext())

        nameList = arrayListOf<String>()
        dateList = arrayListOf<String>()
        timeList = arrayListOf<String>()
        statusList = arrayListOf<String>()
        idList = arrayListOf<String>()

        recyclerView = (view.findViewById(R.id.results_recycler)) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        results_delete.setOnClickListener {
            alertDialog()
        }

        getFinishedWorkouts()
        getHistoryList()
    }

    private fun getFinishedWorkouts() {
        database.getReference("users")
            .child(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                val finished = it.child("finished").value.toString()
                val started = it.child("started").value.toString()

                results_finished.text = finished
                results_started.text = started

                if (started.toInt() == 0) {
                    results_text.visibility = View.VISIBLE
                    results_recycler.visibility = View.GONE
                    results_delete.visibility = View.GONE
                } else {
                    results_recycler.visibility = View.VISIBLE
                    results_delete.visibility = View.VISIBLE
                }
            }
    }

    private fun getHistoryList() {
        database.getReference("history")
            .child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nameList.clear()
                    dateList.clear()
                    timeList.clear()
                    statusList.clear()
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {

                            val name = snap.child("workout").value.toString()

                            val d = snap.child("date").value.toString()
                            val dateFormat = SimpleDateFormat("dd/MM/yy")
                            val timeFormat = SimpleDateFormat("hh:mm")
                            val date = dateFormat.format(d.toLong())
                            val time = timeFormat.format(d.toLong())

                            val id = snap.key.toString()

                            val status = snap.child("status").value.toString()

                            nameList.add(name)
                            dateList.add(date)
                            timeList.add(time)
                            statusList.add(status)
                            idList.add(id)
                        }
                        val adapter = ResultsAdapter(nameList, dateList, timeList, statusList)
                        recyclerView.adapter = adapter
                        adapter.setOnItemClickListener(object: ResultsAdapter.onItemClickListener{
                            override fun onItemClick(position: Int) {
                                val intent = Intent(activity, ResultsDetailsActivity::class.java)
                                intent.putExtra("workout", idList[position])
                                startActivity(intent)
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun alertDialog() {
        builder.setTitle("Are You Sure?")
            .setMessage("Are you sure you want to delete your results history?")
            .setCancelable(true)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener {
                        dialog, id -> deleteHistory()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            .show()
    }

    private fun deleteHistory() {
        database.getReference("users").child(auth.currentUser!!.uid).child("finished").setValue(0)
        database.getReference("users").child(auth.currentUser!!.uid).child("started").setValue(0)
        database.getReference("history").child(auth.currentUser!!.uid).removeValue()

        getFinishedWorkouts()
    }
}