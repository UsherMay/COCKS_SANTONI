package com.yvonbaptiste.todo.user

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.yvonbaptiste.todo.detail.ui.theme.YvonBaptisteTheme

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            val uri: Uri? by remember { mutableStateOf(null) }

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap = it
            }

            fun takePhoto() {
                takePicture.launch()
            }

            YvonBaptisteTheme {
                User(
                    bitmap,
                    uri,
                    takePhoto = ::takePhoto
                )
            }


        }
    }
}

@Composable
fun User(bitmap: Bitmap?, uri: Uri?, takePhoto : () -> Unit) {

    Column {
        AsyncImage(
            modifier = Modifier.fillMaxHeight(.2f),
            model = bitmap ?: uri,
            contentDescription = null
        )
        Button(onClick = { takePhoto() }, content = { Text("Take picture") })
        Button(onClick = {}, content = { Text("Pick photo") })
    }
}



@Preview(showBackground = true)
@Composable
fun UserPreview() {
    YvonBaptisteTheme {
        // User()
    }
}