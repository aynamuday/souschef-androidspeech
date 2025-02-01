package com.samsantech.souschef.data

import android.content.Context

object PreferencesManager {
    private const val PREFS_NAME = "app_prefs"
    private const val DISMISS_BLUETOOTH_CONNECT_PERMISSION_COUNT_KEY = "dismiss_bluetooth_connect_permission_count"

    fun getDismissBluetoothConnectPermissionCount(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(DISMISS_BLUETOOTH_CONNECT_PERMISSION_COUNT_KEY, 0)
    }

    fun incrementDismissBluetoothConnectPermissionCount(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentCount = getDismissBluetoothConnectPermissionCount(context)
        editor.putInt(DISMISS_BLUETOOTH_CONNECT_PERMISSION_COUNT_KEY, currentCount + 1)
        editor.apply()
    }
}