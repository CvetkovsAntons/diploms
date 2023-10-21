package com.example.fitnessapp.Activities

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.Fragments.*
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_workouts.*

class MainActivity() : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private val homePage = HomeFragment()
    private val workoutPage = WorkoutsFragment()
    private val resultsPage = ResultsFragment()
    private val profilePage = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val page = intent.getStringExtra("fragment")

        when (page) {
            "homePage" -> replaceFragment(homePage)
            "profilePage" -> replaceFragment(profilePage)
            "workoutsPage" -> replaceFragment(workoutPage)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.Home -> replaceFragment(homePage)
                R.id.Workouts -> replaceFragment(workoutPage)
                R.id.Results -> replaceFragment(resultsPage)
                R.id.Profile -> replaceFragment(profilePage)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flFragment, fragment)
        transaction.commit()
    }
}