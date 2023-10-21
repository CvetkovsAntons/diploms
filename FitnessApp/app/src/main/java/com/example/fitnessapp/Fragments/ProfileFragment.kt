package com.example.fitnessapp.Fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.fitnessapp.Activities.EditProfileActivity
import com.example.fitnessapp.Activities.SignUpActivity
import com.example.fitnessapp.Database.UserData
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_exercises_description.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var name : String
    private lateinit var height : String
    private lateinit var weight : String
    private lateinit var builder : AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        builder = AlertDialog.Builder(requireActivity())

        profile_edit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            startActivity(intent)
        }

        sign_out_button.setOnClickListener {
            builder.setTitle("Are You Sure?")
                .setMessage("Are you sure you want to sign out?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener {
                            dialog, id -> signOutProfile()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                .show()
        }

        delete_account_button.setOnClickListener {
            builder.setTitle("Are You Sure?")
                .setMessage("Are you sure you want to delete your profile?")
                .setCancelable(true)
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener {
                            dialog, id -> deleteAccount()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                .show()
        }

        loadUserInfo()
    }

    private fun loadUserInfo() {
        database.getReference("users")
            .child(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                name = it.child("name").value.toString()
                height = it.child("height").value.toString()
                weight = it.child("weight").value.toString()

                profile_name.text = name
                profile_height.text = height + " cm"
                profile_weight.text = weight + " kg"

                Glide.with(this@ProfileFragment).load(auth.currentUser!!.photoUrl).circleCrop().skipMemoryCache(true)
                    .into(profile_image)
            }
    }

    private fun signOutProfile() {
        auth.signOut()
        val intent = Intent(requireActivity(), SignUpActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun deleteAccount() {
        val intent = Intent(requireActivity(), SignUpActivity::class.java)
        intent.putExtra("delete", "delete")
        intent.putExtra("user", auth.currentUser!!.uid)
        startActivity(intent)
        requireActivity().finish()
    }
}