package com.example.fitnessapp.Database

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class WorkoutExercisesSetsBodyAdapter(private val setsList : ArrayList<WorkoutExercisesSets>, private val workoutId : String, private val sectionKey : String, private val exercise : String, private val context: Activity) : RecyclerView.Adapter<WorkoutExercisesSetsBodyAdapter.ViewHolder>() {

    lateinit var mContext: Context
    private var database = FirebaseDatabase.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private lateinit var repsEdit : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout_sets_body, parent, false)
        mContext = parent.context
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sets: WorkoutExercisesSets = setsList[position]
        val setNumber = position + 1
        holder.set.text = "SET " + setNumber
        val reps = sets.reps.toString()
        if (reps == "null") {
            holder.reps.text = ""
        } else {
            holder.reps.text = reps
        }
        holder.button.setOnClickListener {
            if (sets.position!!.toInt() != 1) {
                database.getReference("workoutExercises").child(auth.currentUser!!.uid)
                    .child(sectionKey).child(workoutId).child(exercise).child("sets")
                    .child("set_" + sets.position).removeValue()
            } else {
                Toast.makeText(mContext, "Cannot delete this set!", Toast.LENGTH_SHORT).show()
            }
        }

        holder.reps.setOnEditorActionListener { v, actionId, event ->
            repsEdit = holder.reps.text.toString()
            if (repsEdit == "null" || holder.reps.text == null || repsEdit == "" || repsEdit == "0" || repsEdit == " " || repsEdit == "  " ) {
                Toast.makeText(context, "ADD REPS", Toast.LENGTH_SHORT).show()
                true
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                database.getReference("workoutExercises").child(auth.currentUser!!.uid)
                    .child(sectionKey).child(workoutId).child(exercise).child("sets")
                    .child("set_" + sets.position).child("reps").setValue(repsEdit.toLong())
                false
            } else {
                false
            }
        }

//        if (sets.finished == "true") {
//            holder.layout.setCardBackgroundColor(mContext.resources.getColor(R.color.green))
//        }

    }

    override fun getItemCount(): Int {
        return setsList.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var set : TextView = itemView.findViewById(R.id.workout_exercises_sets_set)
        var reps : TextView = itemView.findViewById(R.id.workout_exercises_sets_reps)
        var button : ImageView = itemView.findViewById(R.id.workout_exercises_delete)
        var layout : CardView = itemView.findViewById(R.id.card_sets)

    }
}