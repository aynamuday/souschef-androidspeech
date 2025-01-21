package com.samsantech.souschef.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.FormTextField
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.SelectionCard
import com.samsantech.souschef.ui.components.SkipButton
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun SelectCuisinesScreen(
    activity: ComponentActivity,
    userViewModel: UserViewModel,
    onNavigateToSelectDislikes: () -> Unit
) {
    val preferences by userViewModel.signUpPreferences.collectAsState()

    BackHandler {
        activity.finish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, bottom = 30.dp, start = 32.dp, end = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SkipButton(onClick = {
                userViewModel.clearPreferencesCuisine()
                onNavigateToSelectDislikes()
            })

            Text(
                text = "Type of cuisines you're most interested with?",
                color = Color(0xFF16A637),
                fontSize = 20.sp,
                fontFamily = Konkhmer_Sleokcher,
            )
            Text(
                text = "This will help us curate more recipe experience for you.",
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(12.dp))

            val cuisines = listOf("Filipino", "Korean", "Italian", "American")

            cuisines.forEach { cuisine ->
                val isSelected = preferences.cuisines?.contains(cuisine)

                SelectionCard(
                    text = cuisine,
                    clickable = {
                        if (isSelected == true) {
                            userViewModel.removePreferencesCuisine(cuisine)
                        } else {
                            userViewModel.addPreferencesCuisine(cuisine)
                        }
                    },
                    borderColor = if (isSelected == true) { Green } else Color.Black,
                    backgroundColor = if (isSelected == true) { Green.copy(.2f) } else Color.White,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            val otherCuisine by userViewModel.otherCuisine.collectAsState()

            var showOtherCuisineTextField by remember {
                mutableStateOf(otherCuisine.isNotBlank())
            }

            SelectionCard(
                text = "Others",
                clickable = {
                    if (showOtherCuisineTextField) {
                        userViewModel.otherCuisine.value = ""
                    }
                    showOtherCuisineTextField = !showOtherCuisineTextField
                },
                borderColor = if (otherCuisine.isNotEmpty()) { Green } else Color.Black
            )
            if (showOtherCuisineTextField) {
                FormTextField(
                    value = otherCuisine,
                    onValueChange = {
                        userViewModel.otherCuisine.value = it
                    },
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                    placeholder = "Please specify"
                )
            }
        }

        ColoredButton(
            onClick = onNavigateToSelectDislikes,
            text = "Continue"
        )
    }
}