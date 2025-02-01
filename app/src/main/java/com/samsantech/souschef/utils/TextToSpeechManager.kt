package com.samsantech.souschef.utils

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import java.io.File
import java.util.Locale

//@RequiresApi(Build.VERSION_CODES.Q)
class TextToSpeechManager(context: Context) {
    private val textToSpeech = TextToSpeech(context) { status ->
        if (status != TextToSpeech.SUCCESS) {
            println("ERROR CODE $status")
        }
    }
    private var onSaveDone: ((Boolean) -> Unit)? = null
    private var voiceName = "en-us-x-tpf-network"
    private var language = "en"

    init {
        textToSpeech.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        textToSpeech.setSpeechRate(1f)
        textToSpeech.setPitch(1f)

        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {

            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "100") {
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
        textToSpeech.setLanguage(Locale.forLanguageTag(language))
        textToSpeech.setVoice(Voice(
            voiceName,
            Locale.forLanguageTag(language),
            Voice.QUALITY_VERY_HIGH,
            Voice.LATENCY_NORMAL,
            true,
            null
        ))
        textToSpeech.stop()

        onSaveDone = onDone
        textToSpeech.synthesizeToFile(
            text,
            null,
            file,
            "100"
        )
    }

    fun synthesize(text: String) {
        textToSpeech.setLanguage(Locale.forLanguageTag(language))
        textToSpeech.setVoice(Voice(
            voiceName,
            Locale.forLanguageTag(language),
            Voice.QUALITY_VERY_HIGH,
            Voice.LATENCY_NORMAL,
            true,
            null
        ))
        textToSpeech.stop()

        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_ADD,
            null,
            null
        )
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun destroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    fun changeVoice(voice: com.samsantech.souschef.data.Voice) {
        val updatedLanguage = if (voice.language == "English") "en-US" else "fil-PH"
        val updatedVoiceName = getVoice(voice)

        if (updatedVoiceName != null) {
            voiceName = updatedVoiceName
            language = updatedLanguage
        }
    }

    fun testVoice(voice: com.samsantech.souschef.data.Voice, testText: String) {
        textToSpeech.stop()

        val language = if (voice.language == "English") "en-US" else "fil-PH"
        val voiceName = getVoice(voice)
        if (voiceName != null) {
            textToSpeech.setLanguage(Locale.forLanguageTag(if (voice.language == "English") "en-US" else "fil-PH"))
            textToSpeech.setVoice(Voice(
                voiceName,
                Locale.forLanguageTag(language),
                Voice.QUALITY_VERY_HIGH,
                Voice.LATENCY_NORMAL,
                true,
                null
            ))
        }

        textToSpeech.speak(
            testText,
            TextToSpeech.QUEUE_ADD,
            null,
            null
        )
    }

    private val voices = hashMapOf(
        "en-us-x-tpf-network" to com.samsantech.souschef.data.Voice("English", "Woman", "Default"),
        "en-us-x-tpf-local" to com.samsantech.souschef.data.Voice("English", "Woman", "Normal"),
        "en-us-x-tpc-local" to com.samsantech.souschef.data.Voice("English", "Woman", "Calm"),
        "en-us-x-iol-local" to com.samsantech.souschef.data.Voice("English", "Man", "Default"),
        "en-us-x-iom-network" to com.samsantech.souschef.data.Voice("English", "Man", "Podcast"),
        "fil-ph-x-fic-local" to com.samsantech.souschef.data.Voice("Filipino", "Woman", "Default"),
        "fil-ph-x-cfc-local" to com.samsantech.souschef.data.Voice("Filipino", "Woman", "Normal"),
        "fil-ph-x-fid-network" to com.samsantech.souschef.data.Voice("Filipino", "Man", "Default"),
        "fil-ph-x-fie-network" to com.samsantech.souschef.data.Voice("Filipino", "Man", "Radio"),
    )

    private fun getVoice(voice: com.samsantech.souschef.data.Voice): String? {
        val foundVoice = voices.filter { it.value.gender == voice.gender && it.value.language == voice.language && it.value.variety == voice.variety }
        if (foundVoice.isNotEmpty()) {
            return foundVoice.entries.first().key
        }

        return null
    }
}