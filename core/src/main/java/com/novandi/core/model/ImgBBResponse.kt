package com.novandi.core.model

import com.google.gson.annotations.SerializedName

data class ImgBBResponse(
    @field:SerializedName("data")
    val data: ImgBB,

    @field:SerializedName("success")
    val success: Boolean
)

data class ImgBB(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("image")
    val image: ImgBBImage
)

data class ImgBBImage(
    @field:SerializedName("url")
    val url: String
)