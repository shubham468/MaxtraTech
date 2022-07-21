package com.tech.maxtratech

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tech.umr.Repo.Repository
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainViewModel(private val repository: Repository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val openData = MutableLiveData<MainModelResponse>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun setData(
        name: String,
        user_id: String,
        post_type: String,
        discription: String,
        images: ArrayList<File>,
        videos: File,
        /*video_thumbnail: File,*/
    ) {

        val userId: RequestBody =
            user_id.toRequestBody("text/plain".toMediaTypeOrNull())

        val name1: RequestBody =
            name.toRequestBody("text/plain".toMediaTypeOrNull())

        val post_type1: RequestBody =
            post_type.toRequestBody("text/plain".toMediaTypeOrNull())

        val discription1: RequestBody =
            discription.toRequestBody("text/plain".toMediaTypeOrNull())

        val photo: ArrayList<MultipartBody.Part> = ArrayList()
        for (i in images) {
            val requestBody: RequestBody =
                i.absoluteFile.asRequestBody("image/*".toMediaTypeOrNull())
            photo.add(
                MultipartBody.Part.createFormData(
                    "images[]",
                    i.name,
                    requestBody
                )
            )
            Log.e("multipartphoto", i.absolutePath)
        }

        val requestBody: RequestBody =
            videos.absoluteFile.asRequestBody("video/*".toMediaTypeOrNull())
        val videos1=MultipartBody.Part.createFormData(
            "videos",
            videos.name,
            requestBody
        )
        Log.e("multipartVideo", videos1.toString())

//        val requestBody1: RequestBody =
//            video_thumbnail.absoluteFile.asRequestBody("image/*".toMediaTypeOrNull())
//        val videothumbnail1=MultipartBody.Part.createFormData(
//            "videos",
//            videos.name,
//            requestBody1
//        )
//        Log.e("multipartVideothumb", videothumbnail1.toString())




        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = async {
                repository.getData(
                    name1,
                    userId,
                    post_type1,
                    discription1,
                    photo,
                    videos1,
                    /*videothumbnail1,*/
                )
            }
            withContext(Dispatchers.Main) {
                if (response.await().isSuccessful) {
                    openData.postValue(response.await().body())
                    loading.value = false
                } else {
                    onError("Error : ${response.await().message()} ")
                }
            }
        }
    }

    fun cancelCall() {
        onCleared()
    }


    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}