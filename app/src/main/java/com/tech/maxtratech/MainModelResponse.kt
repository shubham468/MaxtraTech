package com.tech.maxtratech

import java.util.*

data class MainModelResponse(
    val status:String,
    val message: Any,
    val data:MainDetail,
    ) {}

data class MainDetail(
    val created_at: String,
    val discription: String,
    val group_id: Any,
    val id: Int,
    val images: String,
    val name: String,
    val post_type: String,
    val updated_at: String,
    val user_id: Int,
    val video_thumbnail: Any,
    val videos: String
) {

}
