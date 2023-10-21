package com.example.fitnessapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_get_user_name.*

class GetUserNameActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    var getName = this

    companion object {
        val refDb = Firebase.database.getReference("users")
        private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_name)

        name_button.setOnClickListener {
            if (name_field.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter your name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val name: String = name_field.text.toString().trim { it <= ' ' }
                addNameToDb(name)

                val intent = Intent(this, GetUserWeightAndHeightActivity::class.java)
                intent.putExtra("name", name)
                startActivity(intent)
            }
        }
    }

    private fun addNameToDb(name: String) {
        refDb.child(userId).child("name").setValue(name)
    }
}