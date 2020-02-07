package flow.emovent.com.modules.uploadApis

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadAPIs {
    @Multipart
    @POST("/upload")
    fun upload(@Part file: MultipartBody.Part, @Part("name") requestBody: RequestBody): Call<ResponseBody>


}
