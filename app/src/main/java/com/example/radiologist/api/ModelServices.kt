package com.example.radiologist.api

import com.example.radiologist.api.model.ModelResponse
import com.example.radiologist.model.Constants
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ModelServices {
    @Multipart
    @POST(Constants.CHAT_BOT_END_POINT)
    fun getResponseWithImg(
        @Query("prompt") prompt: String,
        @Part image: MultipartBody.Part,
    ): Call<ModelResponse>

}