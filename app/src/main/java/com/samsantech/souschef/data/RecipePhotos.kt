package com.samsantech.souschef.data

import android.net.Uri

data class RecipePhotos(
    var id: String,
    var photosUrl: HashMap<String, Uri> = hashMapOf()
)