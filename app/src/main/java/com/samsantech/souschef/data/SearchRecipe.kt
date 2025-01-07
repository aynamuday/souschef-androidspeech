package com.samsantech.souschef.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchRecipe(
    var id: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var photosUrl: HashMap<String, String> = hashMapOf(),
    var title: String = "",
)
