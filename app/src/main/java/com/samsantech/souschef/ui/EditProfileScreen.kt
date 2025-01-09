package com.samsantech.souschef.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.ConfirmDialog
import com.samsantech.souschef.ui.components.FormOutlinedTextField
import com.samsantech.souschef.ui.components.Header
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.utils.isValidUsername
import com.samsantech.souschef.viewmodel.AuthViewModel
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(
    context: Context,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToUpdateEmail: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToLogin: () -> Unit
) {

    val user by userViewModel.user.collectAsState()

    var displayName by remember {
        mutableStateOf(user?.displayName)
    }
    var username by remember {
        mutableStateOf(user?.username)
    }
    var error by remember {
        mutableStateOf("")
    }
    var loading by remember {
        mutableStateOf(false)
    }
    var showLogoutConfirmation by remember {
        mutableStateOf(false)
    }

    Box {
        Column {
            Header()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 20.dp, start = 25.dp, end = 25.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edit Profile",
                        color = Color(0xFF16A637),
                        fontSize = 25.sp,
                        fontFamily = Konkhmer_Sleokcher,
                        style = LocalTextStyle.current.merge(
                            TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    )

                    displayName?.let {
                        FormOutlinedTextField(
                            value = it,
                            onValueChange = { valueChange ->
                                displayName = valueChange
                            },
                            label = "Name",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    username?.let {
                        FormOutlinedTextField(
                            value = it,
                            onValueChange = { valueChange ->
                                error = ""
                                username = valueChange

                                if (valueChange.isNotBlank() && valueChange != user!!.username) {
                                    userViewModel.isUsernameExists(valueChange) { isExists ->
                                        if (isExists) {
                                            error = "Username is already taken."
                                        }
                                    }

                                    if (!isValidUsername(valueChange)) {
                                        error = "Username must only contain letters, numbers, underscore, and dot."
                                    }
                                }
                            },
                            label = "Username",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                    if (error.isNotBlank()) {
                        Text(
                            text = error,
                            fontSize = 14.sp,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ColoredButton(
                        onClick = {
                            if ((displayName?.isNotEmpty() == true || username?.isNotEmpty() == true) && error.isEmpty()) {
                                loading = true

                                userViewModel.updateProfile(
                                    displayName,
                                    username
                                ) { isSuccess, errorMessage ->
                                    loading = false

                                    if (isSuccess) {
                                        onNavigateToProfile()
                                    } else {
                                        if (errorMessage != null) {
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                                }
                            }
                        },
                        text = "Save"
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ColoredButton(
                        onClick = onNavigateToUpdateEmail,
                        containerColor = Color.White, contentColor = Green,
                        text = "Update Email",
                        border = BorderStroke(1.dp, Green)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ColoredButton(
                        onClick = onNavigateToChangePassword,
                        containerColor = Color.White, contentColor = Green,
                        text = "Change Password",
                        border = BorderStroke(1.dp, Green)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ColoredButton(
                        onClick = {
                            showLogoutConfirmation = true
                        },
                        containerColor = Color.Red, contentColor = Color.White,
                        text = "Logout",
                        border = BorderStroke(1.dp, Color.Red)
                    )
                }
            }
        }

        if (loading) {
            ProgressSpinner()
        }

        if (showLogoutConfirmation) {
            ConfirmDialog(
                message = "Are you sure you want to logout?",
                buttonOkayName = "Yes",
                onClickCancel = { showLogoutConfirmation = false },
                onClickOkay = {
                    loading = true
                    authViewModel.logout()
                    loading = false
                    onNavigateToLogin()
                }
            )
        }
    }
}