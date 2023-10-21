package layout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitnessapp.Database.WorkoutExercisesInfo
import com.example.fitnessapp.Database.WorkoutExercisesSets
import com.example.fitnessapp.Database.WorkoutExercisesSetsAdapter
import com.example.fitnessapp.R

class WorkoutExercisesInfoAdapter(private val context : Context, private val infoList : ArrayList<WorkoutExercisesInfo>) : RecyclerView.Adapter<WorkoutExercisesInfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.workout_exercises, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info : WorkoutExercisesInfo = infoList[position]
        holder.name.text = info.name
        holder.equipment.text = info.equipment
        Glide.with(holder.itemView.context).load(info.image_full).skipMemoryCache(true).into(holder.image)
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.workout_exercises_name)
        var equipment : TextView = itemView.findViewById(R.id.workout_exercises_equipment)
        var image : ImageView = itemView.findViewById(R.id.workout_exercises_image)
    }
}