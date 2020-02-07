package flow.emovent.com.postUploader

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import flow.emovent.com.R
import kotlinx.android.synthetic.main.post_uploader.*
import java.lang.Exception

class PostUploader:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_uploader)

        insertPhoto.setOnClickListener {
            val photoPicker=Intent(Intent.ACTION_PICK)
            photoPicker.type="image/*"
            startActivityForResult(photoPicker,200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try{
            when(resultCode){
                200 -> {TODO("get photo path and import it to recyclerView which it will be add soon")}
            }
        }catch (e:Exception){}
    }
}