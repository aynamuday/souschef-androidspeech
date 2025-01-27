package com.samsantech.souschef.utils

import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build

class BluetoothHelper(private val audioManager: AudioManager) {
    fun bluetoothReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED)
                when(state) {
                    BluetoothHeadset.STATE_CONNECTED -> {
                        startBluetoothSco()
                    }
                    BluetoothHeadset.STATE_DISCONNECTED -> {
                        disableBluetoothSco()
                    }
                }
            }
        }
    }

    val bluetoothProfileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile == BluetoothProfile.HEADSET && proxy != null) {
                if (proxy.connectedDevices.isNotEmpty()) {
                    startBluetoothSco()
                }
            }
        }
        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                disableBluetoothSco()
            }
        }
    }

    fun startBluetoothSco() {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.startBluetoothSco()
        audioManager.isBluetoothScoOn = true
    }

    fun disableBluetoothSco() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.clearCommunicationDevice()
        } else {
            audioManager.mode = AudioManager.MODE_NORMAL
            audioManager.stopBluetoothSco()
            audioManager.isBluetoothScoOn = false
        }
    }
}
