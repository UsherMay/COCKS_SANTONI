package com.yvonbaptiste.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.yvonbaptiste.todo.R
import com.yvonbaptiste.todo.data.Api
import com.yvonbaptiste.todo.databinding.FragmentTaskListBinding
import com.yvonbaptiste.todo.detail.DetailActivity
import com.yvonbaptiste.todo.user.UserActivity
import kotlinx.coroutines.launch

class TaskListFragment : Fragment()
{
    private lateinit var binding: FragmentTaskListBinding
    private val viewModel: TasksListViewModel by viewModels()

    val adapterListener : TaskListListener = object : TaskListListener {
        override fun onClickEdit(task: Task) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("task", task)
            editTask.launch(intent)
        }
        override fun onClickDelete(task: Task) {
            // Supprimer la tâche
            viewModel.remove(task)
        }
    }

    val adapter = TaskListAdapter(adapterListener)

    private val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
        val task = result.data?.getSerializableExtra("task") as Task?
        if (task != null)
            viewModel.add(task)
    }

    private val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
        val task = result.data?.getSerializableExtra("task") as Task?
        if (task != null)
            viewModel.edit(task)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTaskListBinding.inflate(inflater)

        binding.addTaskFab.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }

        binding.imageView.setOnClickListener {
            val intent = Intent(context, UserActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est executée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val user = Api.userWebService.fetchUser().body()!!
            binding.topTextView.text = user.name
            binding.imageView.load(user.avatar) {
                error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
            }
        }
        viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        // Il manque un refresh pour la pdp ?
    }


}
