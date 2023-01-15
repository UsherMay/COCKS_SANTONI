package com.yvonbaptiste.todo.user

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.google.android.material.snackbar.Snackbar
import com.yvonbaptiste.todo.R
import com.yvonbaptiste.todo.data.Api
import com.yvonbaptiste.todo.detail.ui.theme.YvonBaptisteTheme
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class UserActivity : ComponentActivity() {

    // propriété: une URI dans le dossier partagé "Images"
    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }

    private val userViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var uri: Uri? by remember { mutableStateOf(null) }

            fun showMessage(message: String) {
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
            }
            /*
            Take a Picture
             */

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    uri = captureUri
                    lifecycleScope.launch {
                        // On envoie l'image au serveur en convertissant uri en MultipartBody
                        if (uri != null) Api.userWebService.updateAvatar(uri!!.toRequestBody())
                    }
                }
            }

            fun takePic() {
                takePicture.launch(captureUri)
            }

            /*
            Choose Picture from album
             */

            val choosePicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri = it

                lifecycleScope.launch {
                    Api.userWebService.updateAvatar(uri!!.toRequestBody())
                }
            }

            val canChoosePicture = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
                if (allowed)
                    choosePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                else showMessage("Access to Photos is denied")
            }

            fun choosePhotoWithPermission() {
                val camPermission = Manifest.permission.CAMERA
                val permissionStatus = checkSelfPermission(camPermission)
                val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
                val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
                when {
                    isAlreadyAccepted -> choosePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    isExplanationNeeded -> showMessage("To reach Photos, access needs to be granted")
                    else -> {
                        if (Build.VERSION.SDK_INT < 30) // Correspond à Android 10 ou -
                            canChoosePicture.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        else choosePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                }
            }

            /*
            Edit User Settings
             */

            fun isEmailValid(email: String): Boolean {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }

            fun dialogBuilder(editedUser : User, message: String, warning: String) {
                val dialogBuilder = AlertDialog.Builder(this)

                dialogBuilder.setMessage(message)
                    // if the dialog is cancelable
                    .setCancelable(false)
                    .setPositiveButton("No", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })
                    .setNegativeButton("Yes", DialogInterface.OnClickListener {
                            dialog, id ->
                        userViewModel.edit(editedUser)
                        finish()
                    })

                val alert = dialogBuilder.create()
                alert.setTitle(warning)
                alert.show()
            }

            fun editSettings(editedUser: User) {
                if(editedUser.name == "" && editedUser.email == "")
                    showMessage("Settings haven't been changed")
                else if(!isEmailValid(editedUser.email) && editedUser.email != "")
                    showMessage("Email is not valid")
                else if(editedUser.name == "" && isEmailValid(editedUser.email)) {
                    val message = "You haven't updated your name, do you want to continue ?"
                    val warning = "Name not updated"
                    dialogBuilder(editedUser, message, warning)
                }
                else if(editedUser.name != "" && !isEmailValid(editedUser.email)) {
                    val message = "You haven't updated your email, do you want to continue ?"
                    val warning = "Email not updated"
                    dialogBuilder(editedUser, message, warning)
                }
                else {
                    userViewModel.edit(editedUser)
                    finish()
                }
            }

            YvonBaptisteTheme {
                NewUser(
                    uri,
                    takePic = ::takePic,
                    choosePic = ::choosePhotoWithPermission,
                    editSettings = { editedUser -> editSettings(editedUser) },
                    userViewModel = userViewModel
                )
            }
        }
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
fun NewUser(uri: Uri?,
            takePic : () -> Unit,
            choosePic : () -> Unit,
            editSettings : (User) -> Unit,
            userViewModel: UserViewModel)
{
    val user by userViewModel.userStateFlow.collectAsState()
    var editedUser by remember(user) { mutableStateOf(user) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize())
    {
        Text(
            text = "User Settings",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = "Profile picture",
            modifier = Modifier.padding(bottom = 12.dp)
        )

        AsyncImage(
            modifier = Modifier.fillMaxHeight(.2f),
            model = uri ?: user.avatar,
            contentDescription = null,
            error = painterResource(R.drawable.ic_action_name)
        )
        Button(
            onClick = { takePic() },
            content = { Text("Take New Picture") },
            modifier = Modifier
                .padding(top = 12.dp)
                .width(160.dp)
        )
        Button(
            onClick = { choosePic() },
            content = { Text("Choose Picture") },
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = "Account",
            modifier = Modifier.padding(top = 30.dp, bottom = 12.dp)
        )
        OutlinedTextField(
            value = editedUser.name ,
            onValueChange = { editedUser = editedUser.copy(name = it)},
            label = { Text(text = "Name") },
        )
        OutlinedTextField(
            value = editedUser.email,
            onValueChange = { editedUser = editedUser.copy(email = it)},
            label = { Text(text = "Email")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Button(
            onClick = { editSettings(editedUser) },
            content = { Text( "Save account")},
            modifier = Modifier
                .padding(top = 12.dp)
                .width(160.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserPreview() {

    val name by remember { mutableStateOf("") }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize())
    {
        Text(
            text = "User Settings",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = "Profile picture",
            modifier = Modifier.padding(bottom = 12.dp)
        )
        AsyncImage(
            modifier = Modifier.fillMaxHeight(.2f),
            model = null,
            contentDescription = null
        )
        Button(
            onClick = {  },
            content = { Text("Take New Picture") },
            modifier = Modifier
                .padding(top = 12.dp)
                .width(160.dp)
        )
        Button(
            onClick = { },
            content = { Text("Choose Picture") },
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = "Account",
            modifier = Modifier.padding(top = 30.dp, bottom = 12.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { },
            label = { Text(text = "Name") }
        )
        OutlinedTextField(
            value = name ,
            onValueChange = { },
            label = { Text(text = "Email")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Button(
            onClick = { },
            content = { Text( "Save account")},
            modifier = Modifier
                .padding(top = 12.dp)
                .width(160.dp)
        )
    }
}

