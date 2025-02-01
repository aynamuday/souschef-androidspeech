package com.samsantech.souschef.data

import androidx.compose.runtime.MutableState

object NetworkStateProvider {
    lateinit var isNetworkAvailable: MutableState<Boolean>
}