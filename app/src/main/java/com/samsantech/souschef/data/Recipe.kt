package com.samsantech.souschef.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    var id: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var userPhotoUrl: String? = null,
    var photosUrl: HashMap<String, Uri> = hashMapOf(),
    var title: String = "",
    var description: String = "",
    var cookTimeHr: String = "0",
    var cookTimeMin: String = "0",
    var prepTimeHr: String = "0",
    var prepTimeMin: String = "0",
    var serving: String = "1",
    var difficulty: String = "",
    var categories: List<String> = listOf(),
    var ingredients: List<String> = listOf(""),
    var instructions: List<String> = listOf(""),
    var tags: List<String> = listOf(),
    var audience: String = "Public",
    var favoriteRecipes: List<String> = listOf(),
    var ratings: HashMap<String, Double>? = null,
    var averageRating: Float? = null,
    var userRating: Float? = null,
//    var isTikTok: Boolean = false,
//    var postId: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString(),
        userId = parcel.readString(),
        userName = parcel.readString(),
        userPhotoUrl = parcel.readString(),
        photosUrl = parcel.readHashMap(Uri::class.java.classLoader) as HashMap<String, Uri>,
        title = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        cookTimeHr = parcel.readString() ?: "0",
        cookTimeMin = parcel.readString() ?: "0",
        prepTimeHr = parcel.readString() ?: "0",
        prepTimeMin = parcel.readString() ?: "0",
        serving = parcel.readString() ?: "1",
        difficulty = parcel.readString() ?: "",
        categories = parcel.createStringArrayList() ?: listOf(),
        ingredients = parcel.createStringArrayList() ?: listOf(""),
        instructions = parcel.createStringArrayList() ?: listOf(""),
        tags = parcel.createStringArrayList() ?: listOf(),
        audience = parcel.readString() ?: "Public",
        favoriteRecipes = parcel.createStringArrayList() ?: listOf(),
        ratings = parcel.readSerializable() as HashMap<String, Double>?,
    )

    companion object : Parceler<Recipe> {
        override fun Recipe.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(userId)
            parcel.writeString(userName)
            parcel.writeString(userPhotoUrl)
            parcel.writeMap(photosUrl)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeString(cookTimeHr)
            parcel.writeString(cookTimeMin)
            parcel.writeString(prepTimeHr)
            parcel.writeString(prepTimeMin)
            parcel.writeString(serving)
            parcel.writeString(difficulty)
//            parcel.writeStringList(mealTypes)
            parcel.writeStringList(categories)
            parcel.writeStringList(ingredients)
            parcel.writeStringList(instructions)
            parcel.writeStringList(tags)
            parcel.writeString(audience)
            parcel.writeStringList(favoriteRecipes)
            parcel.writeSerializable(ratings)
        }

        override fun create(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }
    }
}
