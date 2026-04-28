package com.samsantech.souschef.data

import java.util.Date

data class User(
    var uid: String = "",
    var displayName: String = "",
    var username: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    var password: String = "",
    var sentEventsCount: Double = 0.0,
    var lastSentEventTimestamp: Date? = null,
    var favoriteRecipes: List<String>? = null
)
