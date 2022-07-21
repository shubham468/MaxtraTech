package com.tech.umr.Network


import com.tech.maxtratech.MainModelResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiEndpoint {



    @Multipart
    @POST("create_post")
    suspend fun update(
        @Part("name") userId: RequestBody,
        @Part("user_id") applicationCaseId: RequestBody,
        @Part("post_type") insuredAreaUser: RequestBody,
        @Part("discription") surveyNo: RequestBody,
        @Part images: List<MultipartBody.Part>,
        @Part videos: MultipartBody.Part,
       /* @Part video_thumbnail: MultipartBody.Part,*/
    ): Response<MainModelResponse>

}