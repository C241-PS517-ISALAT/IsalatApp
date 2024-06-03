//package com.isalatapp.api
//
//import android.content.Context
//import android.net.Uri
//import android.widget.Toast
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.OkHttpClient
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Multipart
//import retrofit2.http.POST
//import retrofit2.http.Part
//import showToast
//import uriToVideoFile
//
//interface ApiService {
//    @Multipart
//    @POST("upload_video_endpoint") // Ganti dengan endpoint upload video di server Anda
//    fun uploadVideo(@Part video: MultipartBody.Part): retrofit2.Call<ResponseBody>
//}
//
//fun uploadVideo(videoUri: Uri?, context: Context) {
//    videoUri?.let { uri ->
//        val file = uriToVideoFile(uri, context)
//        val requestFile = RequestBody.create("video/*".toMediaTypeOrNull(), file)
//        val body = MultipartBody.Part.createFormData("video", file.name, requestFile)
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://yourserver.com/") // Ganti dengan URL server Anda
//            .client(OkHttpClient.Builder().build())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val service = retrofit.create(ApiService::class.java)
//        val call = service.uploadVideo(body)
//        call.enqueue(object : retrofit2.Callback<ResponseBody> {
//            override fun onResponse(call: retrofit2.Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    showToast(context, "Video uploaded successfully")
//                } else {
//                    showToast(context, "Failed to upload video")
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
//                showToast(context, "Failed to upload video: ${t.message}")
//            }
//        })
//    } ?: showToast(context, "No video to upload")
//}
