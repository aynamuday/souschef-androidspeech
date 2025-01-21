package com.samsantech.souschef.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechManager(context: Context) {
    private val textToSpeech = TextToSpeech(context) { status ->
        if (status != TextToSpeech.SUCCESS) {
            println("ERROR CODE $status")
        }
    }

    init {
        textToSpeech.language = Locale.US
    }

    fun synthesize(text: String) {
        textToSpeech.stop()

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_ADD,
            null,
            null
        )
    }

    fun destroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}