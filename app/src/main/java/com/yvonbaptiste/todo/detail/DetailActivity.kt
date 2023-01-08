package com.yvonbaptiste.todo.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yvonbaptiste.todo.detail.ui.theme.YvonBaptisteTheme
import com.yvonbaptiste.todo.tasklist.Task
import java.util.*

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YvonBaptisteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
                    val task = intent?.getSerializableExtra("task") as Task? ?: newTask
                    Detail(task,onValidate = {
                        intent.putExtra("task", it)
                        setResult(RESULT_OK, intent)
                        finish()
                    })
                }
            }
        }
    }
}

@Composable
fun Detail(previousTask:Task, onValidate: (Task) -> Unit) {
    var task by remember { mutableStateOf(previousTask) }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(text = "Task Detail", style = MaterialTheme.typography.h4)
        OutlinedTextField(
            label = { Text(text = "Title") },
            value = task.title,
            onValueChange = { task = task.copy(title = "new title") },
        )
        OutlinedTextField(
            label = { Text(text = "Description") },
            value = task.description,
            onValueChange = { task = task.copy(description = "new description") },
        )
        Button(onClick = {
            // val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
            onValidate(previousTask)
        },shape = RoundedCornerShape(20.dp)
        ) {
            Text(text = "Button with elevation")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    YvonBaptisteTheme {
        val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")
        Detail(previousTask = newTask, {})
    }
}