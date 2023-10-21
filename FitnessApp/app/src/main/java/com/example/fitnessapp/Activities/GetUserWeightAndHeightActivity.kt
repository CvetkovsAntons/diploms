package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_get_user_name.*
import kotlinx.android.synthetic.main.activity_get_user_weight_and_height.*

class GetUserWeightAndHeightActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase

    private lateinit var name : String
    private lateinit var height : String
    private lateinit var weight : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_weight_and_height)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        name = intent.getStringExtra("name").toString()

        height_cm.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (height_cm.text.isEmpty() || height_cm.text.toString() == "0") {
                    Toast.makeText(
                        this,
                        "Please enter your height",
                        Toast.LENGTH_SHORT
                    ).show()
                } else{
                    height_mm.requestFocus()
                    false
                }
                true
            } else {
                false
            }
        }

        height_mm.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                weight_kg.requestFocus()
                false
            } else {
                false
            }
        }

        weight_kg.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (weight_kg.text.isEmpty() || weight_kg.text.toString() == "0") {
                    Toast.makeText(
                        this,
                        "Please enter your weight",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    weight_g.requestFocus()
                    false
                }
                true
            } else {
                false
            }
        }



        weight_button.setOnClickListener {
            if (height_cm.text.isEmpty() || height_cm.text.toString() == "0") {
                Toast.makeText(
                    this,
                    "Please enter your height",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (weight_kg.text.isEmpty() || weight_kg.text.toString() == "0") {
                Toast.makeText(
                    this,
                    "Please enter your weight",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addWeightAndHeightToDb()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("fragment", "homePage")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun addWeightAndHeightToDb() {
        val cm = height_cm.text.toString()
        var mm = height_mm.text.toString()
        val kg = weight_kg.text.toString()
        var g = weight_g.text.toString()

        if (mm == "null" || mm == "" || height_mm.text == null) {
            mm = "0"
        }
        if (g == "null" || g == "" || weight_g.text == null) {
            g = "0"
        }

        height = "$cm.$mm"

        weight = "$kg.$g"

        database.getReference("users").child(auth.currentUser!!.uid).child("height").setValue(height)
        database.getReference("users").child(auth.currentUser!!.uid).child("weight").setValue(weight)
    }
}