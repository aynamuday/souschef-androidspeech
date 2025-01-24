package com.samsantech.souschef.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.samsantech.souschef.viewmodel.SharedViewModel

class CookingAssistantNotificationActionReceiver: BroadcastReceiver() {

    private val sharedViewModel = SharedViewModel()
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "STOP" -> {
                sharedViewModel.stopCookingAssistantService(context)
            }
        }
    }
}