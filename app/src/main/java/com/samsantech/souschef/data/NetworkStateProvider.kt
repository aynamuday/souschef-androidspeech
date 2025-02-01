package com.samsantech.souschef.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object NetworkStateProvider {
    lateinit var isNetworkAvailable: MutableState<Boolean>
}