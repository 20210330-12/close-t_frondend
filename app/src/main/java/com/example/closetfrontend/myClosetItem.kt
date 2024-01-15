package com.example.closetfrontend

import com.google.gson.annotations.SerializedName

data class myClosetItem(val userId: String, val clothId: String, val category: String, val styles: List<String>, val tag: List<String>, val imageUrl: String, val link: List<String>)

data class ClothesResponse(
    @SerializedName("clothes") val clothes: List<Clothes>
)

data class Clothes(
    val id: String,
    val category: String,
    val styles: List<String>,
    val tag: List<String>?,
    @SerializedName("imageUrl") val imageUrl: String,
    val link: List<String>?,
    val user: String,
    @SerializedName("userId")  val userId: String
)