package com.samsantech.souschef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.samsantech.souschef.R

@Composable
fun VoiceCommandsGuide(isWhereToViewTextIsVisible: Boolean, isGoBackIconVisible: Boolean, isGoBackIconClicked: (Boolean) -> Unit, isCloseIconClicked: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color.Gray.copy(.5f), RoundedCornerShape(20.dp))
                .padding(15.dp)
                .zIndex(1f)
                .pointerInput(Unit)
                {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                        }
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ManualUserGuideIcon()
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
                if (isGoBackIconVisible) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 4.dp)
                            .clip(CircleShape)
                            .clickable { isGoBackIconClicked(true) }
                            .padding(5.dp)

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrowback_2_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                        )
                    }
                }

            }

            VoiceCommandsSection()

            if (isWhereToViewTextIsVisible) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "You can view this manual by clicking the Manual icon beside Instructions in the Recipe screen, or the User Guide from the menu bar.",
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(
                modifier = Modifier
                    .height(if (isWhereToViewTextIsVisible) 10.dp else 18.dp)
            )
            AccuracyOfVoiceCommandText()
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun ManualUserGuideIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(22, 166, 55, 255))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.manual_icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(20.dp)
        )
    }
}

@Composable
fun EnableVoiceAssistantSection() {
    Text(
        text = "Enable Voice Assistant",
        modifier = Modifier
            .padding(top = 10.dp),
        fontWeight = FontWeight(600)
    )
    Column(
        modifier = Modifier
            .padding(top = 10.dp, start = 5.dp)
    ) {
        Text(
            text = "To enable voice-activated cooking assistance:",
            fontSize = 15.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(600)
        )
        Text(
            text = "1. Select a recipe you'd like to cook.",
            fontSize = 15.sp,
            lineHeight = 18.sp,
            modifier = Modifier
                .padding(top = 3.dp)
        )
        Text(
            text = "2. Click the Microphone icon beside Instructions.",
            fontSize = 15.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun VoiceCommandUserGuideIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(22, 166, 55, 255))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.voice_icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
        )
    }
}

@Composable
fun VoiceCommandsItem(command: String, meaning: String) {
    Row(
        modifier = Modifier
            .padding(top = 3.dp)
    ) {
        Text(
            text = command,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(600),
            modifier = Modifier
                .weight(.3f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = meaning,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            modifier = Modifier
                .weight(.7f)
        )
    }
}

@Composable
fun VoiceCommandsSection() {
    Text(
        text = "Voice Commands",
        modifier = Modifier
            .padding(top = 10.dp),
        fontWeight = FontWeight(600)
    )
    Column(
        modifier = Modifier
            .padding(top = 10.dp, start = 5.dp)
    ) {
        VoiceCommandsItem("Start", "To start")
        VoiceCommandsItem("Start over", "To start over")
        VoiceCommandsItem("Next", "Proceed to the next instruction")
        VoiceCommandsItem("Go back", "Go back to the previous instruction")
        VoiceCommandsItem("Again", "Repeat the instruction")
        VoiceCommandsItem("Stop", "Pause the voice instruction")
        VoiceCommandsItem("Continue", "Continue the voice instruction")
        VoiceCommandsItem("Skip to [number]", "Skip to a specific instruction")
    }
}

@Composable
fun AccuracyOfVoiceCommandText() {
    Text(
        text = "The accuracy of voice command recognition can be affected by Internet connection and earphone quality (if using one).",
        fontSize = 15.sp,
        lineHeight = 18.sp,
        color = Color.Black.copy(.7f),
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
    )
}