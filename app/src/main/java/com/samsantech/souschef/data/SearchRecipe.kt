package com.samsantech.souschef.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchRecipe(
    var objectID: String? = null,
    var path: String? = null,
    var isTiktok: Boolean? = null,
    var userName: String = "",
    var userPhotoUrl: String = "",
    var photosUrl: HashMap<String, String> = hashMapOf(),
    var title: String = "",
)
