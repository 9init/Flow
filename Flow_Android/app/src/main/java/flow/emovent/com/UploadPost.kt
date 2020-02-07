package flow.emovent.com

import android.os.Bundle
import java.io.File
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import flow.emovent.com.modules.decodeBitmapFromInputStream
import flow.emovent.com.modules.getRealPath
import flow.emovent.com.modules.server
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.mime.FileBody
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.io.entity.EntityUtils
import java.lang.Exception
import java.net.URLEncoder




class UploadPost : AppCompatActivity() {

    lateinit var description: EditText
    lateinit var image:ImageView
    lateinit var insertPhoto:LinearLayout
    lateinit var upload:Button
    lateinit var title:EditText
    private var fillPath:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nested_post_uploader)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        description=findViewById(R.id.description)
        image=findViewById(R.id.image)
        insertPhoto=findViewById(R.id.insertPhoto)

        upload=findViewById(R.id.upload)
        title=findViewById(R.id.title)

    }

    override fun onStart() {
        super.onStart()

        fun openPhotoPicker(){
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,100)
        }
        image.setOnClickListener {openPhotoPicker()}
        insertPhoto.setOnClickListener {openPhotoPicker()}


        upload.setOnClickListener {
            fillPath?.let {
                //Toast.makeText(this@UploadPost, fillPath, Toast.LENGTH_LONG).show()
                val arraylist = ArrayList<File>()
                arraylist.add(File(fillPath))
                val descriptionText=URLEncoder.encode(description.text.toString(), "UTF-8")
                val titleText=URLEncoder.encode(title.text.toString(),"UTF-8")

                val headers=HashMap<String,String>().apply {
                    this["description"]=descriptionText
                    this["title"]=titleText
                    this["user"]="kikozezo33@gmail.com"
                }
                doUpload(arraylist,headers)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(requestCode==100){
                val imageUri = data!!.data
                val path=imageUri!!.path
                val bitmap: Bitmap?
                fillPath=getRealPath(imageUri, this@UploadPost)
                val inputStreamer=this@UploadPost.contentResolver.openInputStream(imageUri)
                bitmap= decodeBitmapFromInputStream(inputStreamer!!)
                image.setImageBitmap(bitmap)
                Toast.makeText(this@UploadPost,path,Toast.LENGTH_LONG).show()
            }
        }catch (e:Throwable){}

    }


    private fun doUpload(files:ArrayList<File>,hashMap: HashMap<String,String>){
        var resCode:Int?=null
        class StartUplaodToServer:AsyncTask<Int,Int,Int>(){
            var responseString:String?=null
            override fun doInBackground(vararg params: Int?): Int {
                return try {


                    val urlString = "http://$server/upload"
                    val httpClient=HttpClientBuilder.create().build()
                    val post= HttpPost(urlString)

                    val iterator=hashMap.entries.iterator()
                    while (iterator.hasNext()){
                        val mapElement = iterator.next() as Map.Entry<String, String>
                        post.addHeader(mapElement.value, mapElement.key)
                    }


                    val reqEntity= MultipartEntityBuilder.create()
                    files.forEachIndexed { _, file ->
                        reqEntity.addPart(file.name, FileBody(file))
                    }
                    post.entity=reqEntity.build()

                    val response =httpClient.execute(post)
                    val resEntity = response.entity
                    val responseStr= EntityUtils.toString(resEntity)
                    responseString=responseStr
                    response.code
                }catch (e:Exception){
                    404
                }
            }

            override fun onPostExecute(statue: Int?) {
                super.onPostExecute(statue)
                when(statue) {
                    200-> Toast.makeText(this@UploadPost,responseString,Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(this@UploadPost,"Error",Toast.LENGTH_LONG).show()
                }
            }

        }
        StartUplaodToServer().execute()
    }


}
