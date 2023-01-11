package com.yvonbaptiste.todo.user

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
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

            /*
            Take a Picture
             */

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap = it

                lifecycleScope.launch {
                    // On envoie l'image au serveur en convertissant bitmap en MultipartBody
                    if(bitmap != null) Api.userWebService.updateAvatar(bitmap!!.toRequestBody())
                }
            }

            fun takePic() {
                takePicture.launch()
            }

            /*
            Choose Picture from album
             */

            val choosePicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri = it

                lifecycleScope.launch {
                    // On envoie l'uri au serveur en convertissant uri en MultipartBody
                    if(uri != null) Api.userWebService.updateAvatar(uri!!.toRequestBody())
                }
            }

            val canChoosePicture = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
                if(allowed) choosePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                // else Permission Denied By The user
            }

            fun canChoosePic() {
                if (Build.VERSION.SDK_INT < 29) // Correspond à Android 10
                    canChoosePicture.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                else choosePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            YvonBaptisteTheme {
                User(
                    bitmap,
                    uri,
                    takePic = ::takePic,
                    choosePic = ::canChoosePic
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

    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }
}

@Composable
fun User(bitmap: Bitmap?, uri: Uri?, takePic : () -> Unit, choosePic : () -> Unit) {
    Column {
        AsyncImage(
            modifier = Modifier.fillMaxHeight(.2f),
            model = bitmap ?: uri,
            contentDescription = null
        )
        Button(onClick = { takePic() }, content = { Text("Take picture") })
        Button(onClick = { choosePic() }, content = { Text("Choose photo") })
    }
}



@Preview(showBackground = true)
@Composable
fun UserPreview() {
    YvonBaptisteTheme {
        // User()
    }
}

