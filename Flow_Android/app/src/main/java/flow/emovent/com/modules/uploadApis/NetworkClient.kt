package flow.emovent.com.modules.uploadApis

import flow.emovent.com.modules.server
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient


class NetworkClient(val query:String=""){
    private val BASE_URL = "http://$server$query"
    private var retrofit: Retrofit? = null
    fun getRetrofitClient(): Retrofit? {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                    .build()
            retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit
    }

}