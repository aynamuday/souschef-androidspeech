package com.samsantech.souschef.data

import android.net.Uri

data class Recipe(
    var id: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var userPhotoUrl: String? = null,
    var photosUrl: HashMap<String, Uri> = hashMapOf(),
    var photosUri: HashMap<String, Uri> = hashMapOf(),
    var title: String = "",
    var description: String = "",
    var cookTimeHr: String = "0",
    var cookTimeMin: String = "0",
    var prepTimeHr: String = "0",
    var prepTimeMin: String = "0",
    var serving: String = "1",
    var difficulty: String = "",
    var mealTypes: List<String> = listOf(),
    var categories: List<String> = listOf(),
    var ingredients: List<String> = listOf(""),
    var instructions: List<String> = listOf(""),
    var tags: List<String> = listOf()
)
