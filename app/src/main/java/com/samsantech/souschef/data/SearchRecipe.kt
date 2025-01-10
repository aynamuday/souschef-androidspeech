package com.samsantech.souschef.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchRecipe(
    var objectID: String? = null,
    var path: String? = null,
    var isTikTok: Boolean? = null,
    var postId: String = "",
    var userName: String = "",
    var userPhotoUrl: String = "",
    var photosUrl: HashMap<String, String> = hashMapOf(),
    var title: String = "",
)
