package com.samsantech.souschef.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.io.File
import java.util.Locale

class TextToSpeechManager(context: Context) {
    private val textToSpeech = TextToSpeech(context) { status ->
        if (status != TextToSpeech.SUCCESS) {
            println("ERROR CODE $status")
        }
    }
    private var onSaveDone: ((Boolean) -> Unit)? = null

    init {
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {

            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId === "100") {
                    onSaveDone?.let { it(true) }
                }
            }

            @Deprecated("Deprecated in Java", ReplaceWith("println(\"error\")"))
            override fun onError(utteranceId: String?) {
                println("error on utterance")
            }
        })
    }

    fun synthesizeToFile(text: String, file: File, onDone: (Boolean) -> Unit) {
        onSaveDone = onDone
        textToSpeech.synthesizeToFile(
            text,
            null,
            file,
            "100"
        )
    }

    fun synthesize(text: String) {
        textToSpeech.stop()

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_ADD,
            null,
            null
        )

        println("hey")
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun destroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}