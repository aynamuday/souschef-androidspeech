package com.samsantech.souschef.data

data class User(
    var uid: String = "",
    var displayName: String = "",
    var username: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    var password: String = "",
    var favoriteRecipes: List<String> = listOf()
)
