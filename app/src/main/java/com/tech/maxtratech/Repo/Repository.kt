package com.tech.umr.Repo

import com.tech.maxtratech.MainModelResponse
import com.tech.umr.Network.ApiEndpoint
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response


class Repository(private val retroInstance: ApiEndpoint) {


    suspend fun getData(
        name: RequestBody,
        user_id: RequestBody,
        post_type: RequestBody,
        discription: RequestBody,
        photo: List<MultipartBody.Part>,
        videos: MultipartBody.Part,
       /* video_thumbnail: MultipartBody.Part,*/

        ): Response<MainModelResponse> = retroInstance.update(
        name,
        user_id,
        post_type,
        discription,
        photo,
        videos,
        /*video_thumbnail*/
    )

}