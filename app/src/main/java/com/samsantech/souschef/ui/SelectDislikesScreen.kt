//package com.samsantech.souschef.ui
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.samsantech.souschef.ui.components.ColoredButton
//import com.samsantech.souschef.ui.components.SelectionCard
//import com.samsantech.souschef.ui.components.SkipButton
//import com.samsantech.souschef.ui.theme.Green
//import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher
//import com.samsantech.souschef.viewmodel.UserViewModel
//
//@Composable
//fun SelectDislikesScreen(
//    userViewModel: UserViewModel,
//    onNavigateToSelectCategories: () -> Unit,
//    onNavigateToSelectSkillLevel: () -> Unit
//) {
//    val preferences by userViewModel.signUpPreferences.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 50.dp, bottom = 30.dp, start = 32.dp, end = 32.dp)
//            .verticalScroll(
//                rememberScrollState()
//            ),
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            SkipButton(onClick = {
//                userViewModel.clearPreferencesDislikes()
//                onNavigateToSelectSkillLevel()
//            })
//
//            Text(
//                text = "Do you have any allergies or dislikes?",
//                color = Color(0xFF16A637),
//                fontSize = 20.sp,
//                fontFamily = Konkhmer_Sleokcher,
//            )
//            Text(
//                text = "This will help us curate more recipe experience for you.",
//                fontStyle = FontStyle.Italic
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//
//            val dislikes = listOf("Shrimp", "Garlic", "Pepper", "Chili", "Milk", "Cheese", "Ketchup")
//
//            dislikes.forEach { dislike ->
//                val isSelected = preferences.dislikes?.contains(dislike)
//
//                SelectionCard(
//                    text = dislike,
//                    clickable = {
//                        if (isSelected == true) {
//                            userViewModel.removePreferencesDislike(dislike)
//                        } else {
//                            userViewModel.addPreferencesDislike(dislike)
//                        }
//                    },
//                    borderColor = if (isSelected == true) { Green } else Color.Black,
//                    backgroundColor = if (isSelected == true) { Green.copy(.2f) } else Color.White,
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//            }
//        }
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            ColoredButton(
//                onClick = onNavigateToSelectCategories,
//                text = "Previous",
//                modifier = Modifier.weight(1f),
//                contentPadding = PaddingValues(12.dp, 12.dp),
//                containerColor = Color.White, contentColor = Color.Black,
//                border = BorderStroke(1.dp, Color.Black)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            ColoredButton(
//                onClick = onNavigateToSelectSkillLevel,
//                text = "Next Step",
//                modifier = Modifier.weight(1f),
//                contentPadding = PaddingValues(12.dp, 12.dp),
//            )
//        }
//    }
//}