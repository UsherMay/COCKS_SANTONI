package com.yvonbaptiste.todo.tasklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yvonbaptiste.todo.databinding.ItemTaskBinding

object TasksDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        // comparaison: est-ce le même "contenu" ? => mêmes valeurs?
        return oldItem.title == newItem.title && oldItem.description == newItem.description;
    }
}

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}

class TaskListAdapter(val listener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksDiffCallback) {

    private lateinit var binding: ItemTaskBinding

    inner class TaskViewHolder(binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description
            binding.taskEditButton.setOnClickListener { listener.onClickEdit(task) }
            binding.taskDeleteButton.setOnClickListener { listener.onClickDelete(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}