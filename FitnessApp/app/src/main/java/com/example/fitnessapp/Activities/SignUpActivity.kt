package com.example.fitnessapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.fitnessapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.protobuf.Value
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database : FirebaseDatabase
    private lateinit var deleteData : String
    private lateinit var user : String

    companion object {
        private const val RC_SIGN_IN = 120
        private const val TAG = "SignInActivity"
        val refDb = Firebase.database.getReference("users")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        database = FirebaseDatabase.getInstance()

        deleteData = intent.getStringExtra("delete").toString()
        user = intent.getStringExtra("user").toString()

        if (deleteData == "delete") {
            Firebase.auth.signOut()
            database.getReference("users")
                .child(user)
                .get()
                .addOnSuccessListener{
                    Log.v("drage", it.value.toString())
                    database.getReference("users")
                        .child(user)
                        .removeValue()
            }.addOnFailureListener {  }

            database.getReference("usersWorkouts")
                .child(user)
                .get()
                .addOnSuccessListener{
                    Log.v("drage", it.value.toString())
                    database.getReference("usersWorkouts")
                        .child(user)
                        .removeValue()
            }.addOnFailureListener {  }

            database.getReference("workoutExercises")
                .child(user)
                .get()
                .addOnSuccessListener{
                    Log.v("drage", it.value.toString())
                    database.getReference("workoutExercises")
                        .child(user)
                        .removeValue()
            }.addOnFailureListener {  }

            database.getReference("history")
                .child(user)
                .get()
                .addOnSuccessListener{
                    Log.v("drage", it.value.toString())
                    database.getReference("history")
                        .child(user)
                        .removeValue()
            }.addOnFailureListener {  }
        }
        mAuth = FirebaseAuth.getInstance()

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        signup_google.setOnClickListener {
            signIn()
        }

        signup_btn.setOnClickListener {
            when {
                TextUtils.isEmpty(et_signup_email.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_signup_password.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    val email: String = et_signup_email.text.toString().trim { it <= ' '}
                    val password: String = et_signup_password.text.toString().trim { it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->

                                if (task.isSuccessful) {

                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                        this,
                                        "You are registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    checkIfUserExists()
                                    createPremadeWorkouts()
                                } else {
                                    Toast.makeText(
                                        this,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                }
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                }
            } else {
                Log.w(TAG, exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkIfUserExists()
                    createPremadeWorkouts()
                }
            }
        }

    private fun checkIfUserExists() {
        refDb.child(mAuth.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    goToNextPage(MainActivity::class.java)
                } else {
                    goToNextPage(GetUserNameActivity::class.java)
                    database.getReference("users").child(mAuth.currentUser!!.uid)
                        .child("finished").setValue(0)
                    database.getReference("users").child(mAuth.currentUser!!.uid)
                        .child("started").setValue(0)
                    database.getReference("users").child(mAuth.currentUser!!.uid)
                        .child("created").setValue(0)
                    database.getReference("usersWorkouts").child(mAuth.currentUser!!.uid)
                        .child("created").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun goToNextPage(activity: Class<*>) {
        val intent = Intent(this, activity)
        when (activity) {
            MainActivity::class.java -> intent.putExtra("fragment", "homePage")
        }
        startActivity(intent)
        finish()
    }

    private fun createPremadeWorkouts() {
        val workoutList = listOf("premadeWorkouts_1", "premadeWorkouts_2", "premadeWorkouts_3", "premadeWorkouts_4", "premadeWorkouts_5")
        val premadeWorkouts_1 = listOf("exercises_1", "exercises_2", "exercises_3", "exercises_4", "exercises_5", "exercises_6", "exercises_7","exercises_8", "exercises_9", "exercises_10")
        val premadeWorkouts_2 = listOf("exercises_19", "exercises_20", "exercises_21", "exercises_22", "exercises_23", "exercises_24")
        val premadeWorkouts_3 = listOf("exercises_11", "exercises_12", "exercises_13", "exercises_14", "exercises_15", "exercises_16", "exercises_17","exercises_18")
        val premadeWorkouts_4 = listOf("exercises_25", "exercises_26", "exercises_27", "exercises_28", "exercises_29", "exercises_30", "exercises_31")
        val premadeWorkouts_5 = listOf("exercises_32", "exercises_33", "exercises_34", "exercises_35", "exercises_36", "exercises_37")

        for (workout in workoutList) {
            val db = database.getReference("workoutExercises").child(mAuth.currentUser!!.uid)
                .child("premadeWorkouts").child(workout)
            var count = 1
            if (workout == "premadeWorkouts_1") {
                for (exercises in premadeWorkouts_1) {
                    db.child(exercises).child("sets")
                        .child("set_$count").child("position").setValue(count)
                }
            }
            if (workout == "premadeWorkouts_2") {
                for (exercises in premadeWorkouts_2) {
                    db.child(exercises).child("sets")
                        .child("set_$count").child("position").setValue(count)
                }
            }
            if (workout == "premadeWorkouts_3") {
                for (exercises in premadeWorkouts_3) {
                    db.child(exercises).child("sets")
                        .child("set_$count").child("position").setValue(count)
                }
            }
            if (workout == "premadeWorkouts_4") {
                for (exercises in premadeWorkouts_4) {
                    db.child(exercises).child("sets")
                        .child("set_$count").child("position").setValue(count)
                }
            }
            if (workout == "premadeWorkouts_5") {
                for (exercises in premadeWorkouts_5) {
                    db.child(exercises).child("sets")
                        .child("set_$count").child("position").setValue(count)
                }
            }
        }

    }
}
