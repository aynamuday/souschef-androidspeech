package com.samsantech.souschef.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechToTextManager(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private var result: ((String) -> Unit)? = null

    fun startStreaming(result: (String) -> Unit) {
        resetSpeechRecognizer()
        setRecogniserIntent()
        startListening()
        this.result = result
    }

    fun stopStreaming() {
        speechRecognizer!!.cancel()
        speechRecognizer!!.stopListening()
        speechRecognizer!!.destroy()
    }

    private fun startListening() {
        speechRecognizer!!.startListening(recognizerIntent)
    }

    private fun resetSpeechRecognizer() {
        if (speechRecognizer != null) speechRecognizer!!.destroy()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        if (SpeechRecognizer.isRecognitionAvailable(context))
            speechRecognizer!!.setRecognitionListener(mRecognitionListener)
        else speechRecognizer!!.destroy()
    }

    private val mRecognitionListener = object : RecognitionListener {
        override fun onBeginningOfSpeech() {}

        override fun onBufferReceived(buffer: ByteArray) {}

        override fun onEndOfSpeech() {
            speechRecognizer!!.stopListening()
        }

        override fun onResults(results: Bundle) {
            val matches: ArrayList<String>? = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            for (text in matches!!) {
                result?.invoke(text)
                println(text)
            }

            startListening()
        }

        override fun onError(errorCode: Int) {
            println("STT ERROR $errorCode")

            resetSpeechRecognizer()
            startListening()
        }

        override fun onEvent(arg0: Int, arg1: Bundle) {}

        override fun onPartialResults(arg0: Bundle) {}

        override fun onReadyForSpeech(arg0: Bundle) {}

        override fun onRmsChanged(rmsdB: Float) {}
    }

    private fun setRecogniserIntent() {
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en-US"
        )
        recognizerIntent!!.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            3000
        )
    }
}