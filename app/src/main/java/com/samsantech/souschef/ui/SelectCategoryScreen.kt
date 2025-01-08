package com.samsantech.souschef.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samsantech.souschef.R
import com.samsantech.souschef.ui.components.Header
import com.samsantech.souschef.ui.theme.Green
import com.samsantech.souschef.ui.theme.Konkhmer_Sleokcher

@Composable
fun SelectCategoryScreen() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Header()
        Text(
            text = "Craving For?",
            fontFamily = Konkhmer_Sleokcher,
            fontSize = 32.sp,
            color = Green,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        CategoryCard()
        CategoryCard()
        CategoryCard()
        CategoryCard()
        CategoryCard()
        CategoryCard()
    }
}

@Composable
fun CategoryCard() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {  }
    ) {
        Image(
            painter = painterResource(id = R.drawable.chicken_adobo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(.3f)),
        )
        Text(
            text = "Pork",
            fontFamily = Konkhmer_Sleokcher,
            fontSize = 32.sp,
            color = Color.White
        )
    }
}