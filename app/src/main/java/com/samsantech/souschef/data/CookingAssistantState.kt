package com.samsantech.souschef.data

data class CookingAssistantState(
    var isCooking: Boolean = false,
    var recipe: Recipe? = null,
    var currentStep: Int = 1,
    var command: String = ""
)