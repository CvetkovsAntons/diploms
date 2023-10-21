package com.example.fitnessapp.Activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var name : String
    private lateinit var height : String
    private lateinit var weight : String
    private lateinit var builder : AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        builder = AlertDialog.Builder(this)

        name = intent.getStringExtra("name").toString()
        height = intent.getStringExtra("height").toString()
        weight = intent.getStringExtra("weight").toString()

        profile_edit_save.setOnClickListener {
            saveUserInfo()
        }

        profile_edit_cancel.setOnClickListener {
            alertDialog()
        }

        getUserInfo()
    }



    private fun getUserInfo() {
        profile_edit_name.setText(name)
        profile_edit_height.setText(height)
        profile_edit_weight.setText(weight)
    }



    private fun saveUserInfo() {
        name = profile_edit_name.text.toString()
        height = profile_edit_height.text.toString()
        weight = profile_edit_weight.text.toString()

        val refDb = database.getReference("users").child(auth.currentUser!!.uid)

        refDb.child("name").setValue(name)
        refDb.child("height").setValue(height)
        refDb.child("weight").setValue(weight)

        super.onBackPressed()
    }



    private fun alertDialog() {
        builder.setTitle("Are You Sure?")
            .setMessage("Are you sure you want to cancel profile editing?")
            .setCancelable(true)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener {
                        dialog, id -> super.onBackPressed()
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            .show()
    }

    override fun onBackPressed() {
        alertDialog()
    }

}