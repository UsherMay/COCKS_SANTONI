package com.yvonbaptiste.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yvonbaptiste.todo.R
import com.yvonbaptiste.todo.databinding.FragmentTaskListBinding
import com.yvonbaptiste.todo.detail.DetailActivity
import java.util.*

class TaskListFragment : Fragment()
{
    private lateinit var binding: FragmentTaskListBinding
    private val adapter = TaskListAdapter()

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
        val task = result.data?.getSerializableExtra("task") as Task
        taskList = taskList + task
        refreshAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(layoutInflater)
        adapter.submitList(taskList)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter

        binding.addTaskFab.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            // startActivity(intent)
            createTask.launch(intent)

            // val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            // taskList = taskList + newTask


        }
    }

    fun refreshAdapter() {
        adapter.submitList(taskList)
        adapter.notifyDataSetChanged()
    }

    /*
    // "implémentation" de la lambda dans le fragment:
    adapter.onClickDelete = { task ->
    // Supprimer la tâche
    }
    */


}
