package com.samsantech.souschef.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.samsantech.souschef.R
import com.samsantech.souschef.data.Voice
import com.samsantech.souschef.ui.theme.Green

@Composable
fun ManageVoiceSettings(
    voice: Voice,
    isCloseIconClicked: (Boolean) -> Unit,
    onTry: (Voice) -> Unit,
    onSave: (Voice) -> Unit
) {
    var selectGender by remember {
        mutableStateOf(false)
    }
    var gender by remember {
        mutableStateOf(voice.gender)
    }
    var selectLanguage by remember {
        mutableStateOf(false)
    }
    var language by remember {
        mutableStateOf(voice.language)
    }
    var selectVariety by remember {
        mutableStateOf(false)
    }
    var variety by remember {
        mutableStateOf(voice.variety)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(.3f))
            .pointerInput(Unit) {
                detectTapGestures {
                    isCloseIconClicked(true)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color.Gray.copy(.5f), RoundedCornerShape(20.dp))
                .padding(15.dp)
                .zIndex(10f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(22, 166, 55, 255))
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.music),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 5.dp, end = 5.dp)
                        .clip(CircleShape)
                        .clickable { isCloseIconClicked(true) }
                        .padding(8.dp)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Manage Voice Settings",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            DropdownMenu(
                displayText = language,
                expanded = selectLanguage,
                onToggleExpand = { selectLanguage = !selectLanguage },
                selection = listOf("English", "Filipino")
            ) { selected ->
                language = selected
                selectLanguage = false
                variety = "Default"
            }
            Spacer(modifier = Modifier.height(10.dp))
            DropdownMenu(
                displayText = gender,
                expanded = selectGender,
                onToggleExpand = { selectGender = !selectGender },
                selection = listOf("Woman", "Man")
            ) { selected ->
                gender = selected
                selectGender = false
                variety = "Default"
            }
            Spacer(modifier = Modifier.height(10.dp))
            val voices = hashMapOf(
                "en-female" to listOf("Default", "Normal", "Calm"),
                "en-male" to listOf("Default", "Podcast"),
                "fil-female" to listOf("Default", "Normal"),
                "fil-male" to listOf("Default", "Radio")
            )
            val selection = if (language == "English" && gender == "Woman") {
                "en-female"
            } else if (language == "English" && gender == "Man") {
                "en-male"
            } else if (language == "Filipino" && gender == "Woman") {
                "fil-female"
            } else {
                "fil-male"
            }
            voices[selection]?.let {
                DropdownMenu(
                    displayText = "Voice:  $variety",
                    expanded = selectVariety,
                    onToggleExpand = { selectVariety = !selectVariety },
                    selection = it
                ) { selected ->
                    variety = selected
                    selectVariety = false
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ColoredButton(
                onClick = { onTry(Voice(language, gender, variety)) },
                text = "Try",
                contentColor = Green,
                containerColor = Color.White,
                border = BorderStroke(1.dp, Green)
            )
            ColoredButton(
                onClick = { onSave(Voice(language, gender, variety)) },
                text = "Save"
            )
        }
    }
}