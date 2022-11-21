package com.yvonbaptiste.todo.tasklist

import android.app.ActivityManager.TaskDescription
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yvonbaptiste.todo.R

class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    var currentList: List<Task> = emptyList()

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.task_title)
        val textViewDescription = itemView.findViewById<TextView>(R.id.task_description)
        fun bind(taskTitle: String, taskDescription: String) {
            // on affichera les données ici
            textView.text = taskTitle
            textViewDescription.text = taskDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // TODO_DONE("Not yet implemented")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // TODO_DONE("Not yet implemented")
        // strange but so be it
        holder.bind(currentList[position].title,currentList[position].description)
    }

    override fun getItemCount(): Int {
        // TODO_DONE("Not yet implemented")
        return currentList.count()
    }
}