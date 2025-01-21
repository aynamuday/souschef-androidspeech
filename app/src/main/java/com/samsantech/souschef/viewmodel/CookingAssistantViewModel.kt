package com.samsantech.souschef.viewmodel

import android.content.Context
import com.samsantech.souschef.data.CookingAssistantState
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.utils.SpeechToTextManager
import com.samsantech.souschef.utils.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class CookingAssistantViewModel(
    private val context: Context
) {
    private var speechToTextManager: SpeechToTextManager = SpeechToTextManager(context)
    private var textToSpeechManager: TextToSpeechManager? = null
    private var cookingAssistantState: MutableStateFlow<CookingAssistantState> = MutableStateFlow(
        CookingAssistantState()
    )

    private val introText = "Hi, I'm Souschef. I will guide you in this cooking session. To start, say \"Start\"."

    fun startCookingAssistance(recipe: Recipe) {
        cookingAssistantState.value = CookingAssistantState(isCooking = true, recipe = recipe)
        speechToTextManager.startStreaming { result ->
            handleRecognizedCommands(result)
        }
        textToSpeechManager = TextToSpeechManager(context)
        textToSpeechManager?.synthesize(introText)
    }

    fun stopCookingAssistance() {
        speechToTextManager.stopStreaming()
        textToSpeechManager?.destroy()
    }

    private fun handleRecognizedCommands(transcription: String) {
        var currentStep = cookingAssistantState.value.currentStep
        var currentInstruction = cookingAssistantState.value.recipe?.instructions?.get(currentStep-1)
        val totalInstructions = cookingAssistantState.value.recipe?.instructions?.size

        //"start over"
        if (transcription.contains("start over")) {
            textToSpeechManager?.synthesize(introText)
            updateCurrentStep(1)
        }
        //"start"
        else if(transcription.contains("start")) {
            if (currentStep == 1) {
                val instruction = cookingAssistantState.value.recipe?.instructions?.get(0)
                if (instruction != null) {
                    textToSpeechManager?.synthesize(instruction)
                }
            } else {
                textToSpeechManager?.synthesize("You are in Step Number $currentStep. To start over, say \"Start Over\".")
            }
        }
        //"next"
        else if (transcription.contains("next")) {
            if(currentStep == totalInstructions) {
                textToSpeechManager?.synthesize("You've already come to the last instruction.")
            } else {
                val instruction = cookingAssistantState.value.recipe?.instructions?.get(currentStep+1)
                updateCurrentStep(currentStep+1)
                textToSpeechManager?.synthesize(instruction!!)
            }
        }
        //"go back"
        else if(transcription.contains("go back")) {
            if (currentStep <= 1) {
                textToSpeechManager?.synthesize("You are in the first step - cannot go back")
            } else {
                val instruction = cookingAssistantState.value.recipe?.instructions?.get(currentStep-1)
                updateCurrentStep(currentStep-1)
                textToSpeechManager?.synthesize(instruction!!)
            }
        }
        //"again"
        else if(transcription.contains("again")) {
            val instruction = cookingAssistantState.value.recipe?.instructions?.get(currentStep)
            textToSpeechManager?.synthesize(instruction!!)
        }
        //"stop"
        else if (transcription.contains("stop")) {

        }
        //"continue"
        else if (transcription.contains("continue")) {

        }
        //"skip to"
        else if (transcription.contains("skip to")) {

        }
    }

    private fun updateCurrentStep(step: Int) {
        cookingAssistantState.update { currentState ->
            currentState.copy(currentStep = step)
        }
    }
}