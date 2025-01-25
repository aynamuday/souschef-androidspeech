package com.samsantech.souschef.viewmodel

import android.content.Context
import android.media.MediaPlayer
import com.samsantech.souschef.data.CookingAssistantState
import com.samsantech.souschef.data.InstructionsAudioLocalDataSource
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.utils.SpeechToTextManager
import com.samsantech.souschef.utils.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class CookingAssistantViewModel(
    context: Context,
    private val textToSpeechManager: TextToSpeechManager
) {
    private var speechToTextManager: SpeechToTextManager = SpeechToTextManager(context)
    var cookingAssistantState: MutableStateFlow<CookingAssistantState> = MutableStateFlow(
        CookingAssistantState()
    )
    private var toNotDetect: List<String> = listOf()
    private var mediaPlayer: MediaPlayer? = null
    private val instructionsAudioLocalDataSource: InstructionsAudioLocalDataSource = InstructionsAudioLocalDataSource(context)
    private val instructionsAudioDir = instructionsAudioLocalDataSource.getInstructionsAudioDir()
    private val introText = "Hi, I'm SousChef. I will guide you in this cooking session. To start, say \"Start\"."
    private val commands = listOf("start over", "start", "next", "go back", "again", "stop", "continue", "skip to")

//    @RequiresApi(Build.VERSION_CODES.Q)
    fun startCookingAssistance(recipe: Recipe) {
        cookingAssistantState.value = CookingAssistantState(isCooking = true, recipe = recipe)
        speechToTextManager.startStreaming { result ->
            var transcription = result
            if (toNotDetect.isNotEmpty()) {
                toNotDetect.forEach {
                    transcription = transcription.replaceFirst(it, "")
                }
            }
            updateCookingAssistantStateCommand(transcription)
            handleRecognizedCommands(transcription)
            toNotDetect = listOf()
        }
        mediaPlayer = MediaPlayer()
        instructionsAudioLocalDataSource.clearInstructionsAudioDirectory()

        textToSpeechManager.synthesize(introText)
        toNotDetect = toNotDetect.plus("to start")
        toNotDetect = toNotDetect.plus("say start")
    }

    fun stopCookingAssistance() {
        speechToTextManager.stopStreaming()
        textToSpeechManager.stop()
        mediaPlayer?.release()
        instructionsAudioLocalDataSource.clearInstructionsAudioDirectory()
        cookingAssistantState.value = CookingAssistantState()
    }

//    @RequiresApi(Build.VERSION_CODES.Q)
    private fun handleRecognizedCommands(transcription: String) {
        val currentStep = cookingAssistantState.value.currentStep
        val totalInstructions = cookingAssistantState.value.recipe?.instructions?.size

        //"start over"
        if (transcription.contains("start over")) {
            textToSpeechManager.synthesize(introText)
            updateCurrentStep(1)
        }
        //"start"
        else if(transcription.contains("start")) {
            if (currentStep == 1) {
                synthesizeInstructionAndPlay(currentStep)
            } else {
                textToSpeechManager.synthesize("You are in Step Number $currentStep. To start over, say \"Start Over\".")
                toNotDetect = toNotDetect.plus("start over")
                toNotDetect = toNotDetect.plus("start over")
            }
        }
        //"next"
        else if (transcription.contains("next")) {
            if(currentStep == totalInstructions) {
                textToSpeechManager.synthesize("You've already come to the last instruction.")
            } else {
                synthesizeInstructionAndPlay(currentStep+1)
                updateCurrentStep(currentStep+1)
            }
        }
        //"go back"
        else if(transcription.contains("go back")) {
            if (currentStep <= 1) {
                textToSpeechManager.synthesize("You are in the first step - cannot go back")
                toNotDetect = toNotDetect.plus("cannot go back")
            } else {
                synthesizeInstructionAndPlay(currentStep-1)
                updateCurrentStep(currentStep-1)
            }
        }
        //"again"
        else if(transcription.contains("again")) {
            if (mediaPlayer != null) {
                mediaPlayer!!.start()
            }
        }
        //"stop"
        else if (transcription.contains("stop")) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer!!.pause()
            }
        }
        //"continue"
        else if (transcription.contains("continue")) {
            if (mediaPlayer?.isPlaying == false) {
                mediaPlayer!!.start()
            }
        }
        //"skip to"
        else if (transcription.contains("skip to")) {
            val skipToNumber = getSkipToNumber(transcription)
            if (skipToNumber != null) {
                if (skipToNumber <= totalInstructions!!) {
                    updateCurrentStep(skipToNumber)
                    synthesizeInstructionAndPlay(skipToNumber)
                } else {
                    textToSpeechManager.synthesize("There is no Step Number $skipToNumber.")
                }
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.Q)
    private fun synthesizeInstructionAndPlay(instructionNumber: Int) {
        textToSpeechManager.stop()
        var instruction = cookingAssistantState.value.recipe?.instructions?.get(instructionNumber-1)
        val totalInstructions = cookingAssistantState.value.recipe?.instructions?.size

        commands.forEach {
            if (instruction?.contains(it) == true) {
                toNotDetect = toNotDetect.plus(it)
            }
        }

        if (instructionNumber == totalInstructions) {
            instruction = "This is the last step. ".plus(instruction)
        }

        val instructionFileName = "instruction_".plus(instructionNumber.toString())
        var audioFile: File? = instructionsAudioLocalDataSource.getInstructionAudioFile(instructionFileName)

        if (audioFile == null) {
            if (instruction != null) {
                audioFile = File(instructionsAudioDir, instructionFileName)
                textToSpeechManager.synthesizeToFile(instruction, audioFile) {
                    if (it) {
                        playAudioFile(audioFile)
                    }
                }
            }
        } else {
            playAudioFile(audioFile)
        }
    }

    private fun playAudioFile(audioFile: File?) {
        mediaPlayer?.apply {
            reset()
            setOnCompletionListener(null)
            setDataSource(audioFile?.absolutePath)
            prepareAsync()
            setOnPreparedListener {
                start()
            }
        }
    }

    private fun updateCurrentStep(step: Int) {
        cookingAssistantState.update { currentState ->
            currentState.copy(currentStep = step)
        }
    }

    private fun updateCookingAssistantStateCommand(command: String) {
        cookingAssistantState.update { currentState ->
            currentState.copy(command = command)
        }
    }

    private fun getSkipToNumber(command: String): Int? {
        val regex = Regex("""\bskip\b\s+\bto\b\s+(\w+)""", RegexOption.IGNORE_CASE)
        val match = regex.find(command)
        var skipToNumber: Int? = null

        if (match != null) {
            val num = match.groupValues[1]
            val oneToTen = mapOf(
                "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
                "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9
            )

            skipToNumber = num.toIntOrNull()
            if (skipToNumber == null) {
                skipToNumber = oneToTen[num.lowercase()]
            }
        }

        return skipToNumber
    }
}