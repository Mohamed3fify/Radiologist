package com.example.radiologist.api.model

import com.google.gson.annotations.SerializedName

data class ModelResponse(

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

)