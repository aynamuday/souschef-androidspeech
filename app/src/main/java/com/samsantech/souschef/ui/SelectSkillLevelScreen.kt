package com.samsantech.souschef.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.ui.components.ColoredButton
import com.samsantech.souschef.ui.components.ProgressSpinner
import com.samsantech.souschef.ui.components.SelectionCard
import com.samsantech.souschef.ui.components.Dialog
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
import com.samsantech.souschef.viewmodel.UserViewModel

@Composable
fun SelectSkillLevelScreen(
    userViewModel: UserViewModel,
    onNavigateToSelectDislikes: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val preferences by userViewModel.signUpPreferences.collectAsState()
    var loading by remember {
        mutableStateOf(false)
    }
    var success by remember {
        mutableStateOf(false)
    }

    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 70.dp, start = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "How skilled are you in the kitchen?",
                    color = Color(0xFF16A637),
                    fontSize = 20.sp,
                    fontFamily = Konkhmer_Sleokcher,
                )
                Text(
                    text = "This will help us curate more recipe experience for you.",
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(12.dp))

                val skillLevels = listOf("Beginner", "Intermediate", "Advanced")

                skillLevels.forEach {  skillLevel ->
                    val selectedSkillLevel = preferences.skillLevel

                    SelectionCard(
                        text = skillLevel,
                        clickable = {
                            if (selectedSkillLevel == skillLevel) {
                                userViewModel.clearPreferencesSkillLevel()
                            } else {
                                userViewModel.setPreferencesSkillLevel(skillLevel)
                            }
                        },
                        borderColor = if (selectedSkillLevel == skillLevel) { Green } else Color.Black,
                        backgroundColor = if (selectedSkillLevel == skillLevel) { Green.copy(.2f) } else Color.White,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ColoredButton(
                    onClick = onNavigateToSelectDislikes,
                    text = "Previous",
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp, 12.dp),
                    containerColor = Color.White, contentColor = Color.Black,
                    border = BorderStroke(1.dp, Color.Black)
                )
                Spacer(modifier = Modifier.width(12.dp))
                ColoredButton(
                    onClick = {
                        if (preferences.cuisines?.isEmpty() == true && preferences.dislikes?.isEmpty() == true && preferences.skillLevel.isEmpty()) {
                            loading = true

                            userViewModel.setUserPreferences() {
                                loading = false
                                success = true
                            }
                        } else {
                            userViewModel.setUserPreferences {
                                println()
                            }
                            onNavigateToHome()
                        }
                    },
                    text = "Complete",
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp, 12.dp),
                )
            }
        }
        if (loading) {
            ProgressSpinner()
        }

        if (success){
            Dialog(
                icon = "success",
                message = "All done!",
                subMessage = "Thank you for helping us get to know your preferences.",
                onCloseClick = onNavigateToHome
            )
        }
    }
}