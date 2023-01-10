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
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.yvonbaptiste.todo.data.Api
import com.yvonbaptiste.todo.detail.ui.theme.YvonBaptisteTheme
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }


            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                lifecycleScope.launch {
                    val webService = Api.userWebService
                    bitmap?.let { webService.updateAvatar(it.toRequestBody()) }
                }
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

    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use* se charge de faire open et close
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
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

