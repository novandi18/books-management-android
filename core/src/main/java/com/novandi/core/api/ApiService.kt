package com.novandi.core.api

import com.novandi.core.model.ImgBBResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") key: String = "1347a519012465f65ba49b50d5a41588",
        @Part image: MultipartBody.Part
    ) : ImgBBResponse
}