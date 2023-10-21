package com.example.fitnessapp.Database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import org.w3c.dom.Text

class ResultsAdapter(private val name : ArrayList<String>, private val date : ArrayList<String>, private val time : ArrayList<String>, private val status : ArrayList<String>) : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.results_workout, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ResultsAdapter.ViewHolder, position: Int) {
        holder.name.text = name[position]
        holder.date.text = date[position]
        holder.time.text = time[position]
        holder.exercises.text = status[position]
    }

    override fun getItemCount(): Int {
        return name.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var name : TextView = itemView.findViewById(R.id.results_name)
        var date : TextView = itemView.findViewById(R.id.results_date)
        var time : TextView = itemView.findViewById(R.id.results_time)
        var exercises : TextView = itemView.findViewById(R.id.results_exercises)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}