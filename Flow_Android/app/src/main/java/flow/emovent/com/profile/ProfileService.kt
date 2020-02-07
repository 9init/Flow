package flow.emovent.com.profile

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*
import flow.emovent.com.R
import flow.emovent.com.UploadPost
import flow.emovent.com.modules.*
import flow.emovent.com.modules.uploadApis.NetworkClient
import flow.emovent.com.modules.uploadApis.UploadAPIs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class ProfileService :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nested_scroll_profile)
        findViewById<ConstraintLayout>(R.id.constraintProfile).requestFocus()

        window.navigationBarColor=ContextCompat.getColor(this,R.color.colorAccent)


        val recyclerView=findViewById<RecyclerView>(R.id.recycler)

        var resString:String?=null
        class getPosts:AsyncTask<Int?,Int?,Int?>() {
            override fun doInBackground(vararg params: Int?): Int? {
                val url=URL("http://$server/getPosts")

                return try{
                    with(url.openConnection() as HttpURLConnection){
                        requestMethod="POST"
                        resString=if(responseCode==HttpURLConnection.HTTP_OK) inputStream.bufferedReader().readLine()
                        else errorStream.bufferedReader().readLine()
                       responseCode
                    }
                }catch (e:Exception){
                    resString=e.message
                    null
                }
            }

            override fun onPostExecute(responseCode: Int?) {
                super.onPostExecute(responseCode)

                when(responseCode){
                    200-> {
                        val strList = splitQueriesList(StringBuilder(resString!!))
                        recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@ProfileService)
                            adapter = ViewAdapter(strList, this@ProfileService)
                        }
                    }
                    null-> Toast.makeText(this@ProfileService,"Something went wrong",Toast.LENGTH_LONG).show()
                    else-> Toast.makeText(this@ProfileService,"result is : $resString", Toast.LENGTH_LONG).show()

                }

                responseCode?.let{
                    //get posts from the server
                    Log.d("Posts","result is : $resString")

                }?:run {
                    Log.d("Posts","Something went wrong")
                }
            }
        }

        getPosts().execute()




        findViewById<Button>(R.id.upload).setOnClickListener {
            val intent=Intent(this@ProfileService,UploadPost::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

    }


    private fun uploadToServer(filePath: String) {

        val retrofit = NetworkClient().getRetrofitClient()
        val uploadAPIs = retrofit!!.create(UploadAPIs::class.java)
        //Create a file object using file path
        val file = File(filePath)

        MimeTypeMap.getFileExtensionFromUrl(filePath)
        // Create a request body with file and file media type
        val fileReqBody = RequestBody.create(MediaType.parse(getMimeType(filePath)), file)
        // Create MultipartBody.Part using file request-body,file name and part name
        val part = MultipartBody.Part.createFormData("upload", file.name, fileReqBody)
        //Create request body with text description and text media type
        val description = RequestBody.create(MediaType.parse("text/plain"), "image-type")

        val call = uploadAPIs.upload(part, description)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val result=response.body()?.string()
                if(response.code()==200) Toast.makeText(this@ProfileService ,result ,Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProfileService,t.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }
}

