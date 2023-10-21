package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.example.fitnessapp.Fragments.HomeFragment
import com.example.fitnessapp.Fragments.ProfileFragment
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if(user != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", "homePage")
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
        }, 2000)
    }
}