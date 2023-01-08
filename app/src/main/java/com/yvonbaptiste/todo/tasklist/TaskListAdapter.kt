package com.yvonbaptiste.todo.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yvonbaptiste.todo.R

object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        // comparaison: est-ce le même "contenu" ? => mêmes valeurs?
        // (avec data class: simple égalité)
        return oldItem == newItem;
    }
}
interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}
/*
class TaskListAdapter(val listener: TaskListListener) : ... {
    // use: listener.onClickDelete(task)
}

class TaskListFragment : Fragment {
    val adapterListener : TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {...}
        override fun onClickEdit(task: Task) {...}
    }
    val adapter = TaskListAdapter(adapterListener)
}
*/
class TaskListAdapter(val listener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback) {

    //var currentList: List<Task> = emptyList()
    var onClickDelete: (Task) -> Unit = {}
    var onClickEdit: (Task) -> Unit = {}

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.task_title)
        val textViewDescription = itemView.findViewById<TextView>(R.id.task_description)
        val editButton = itemView.findViewById<ImageButton>(R.id.task_edit_button)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.task_delete_button)
        fun bind(task: Task) {
            // on affichera les données ici
            textView.text = task.title
            textViewDescription.text = task.description
            editButton.setOnClickListener { listener.onClickEdit(task) }
            deleteButton.setOnClickListener { listener.onClickDelete(task) }
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
        holder.bind(currentList[position])
        onClickDelete(currentList[position])
    }
}