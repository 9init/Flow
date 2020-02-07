package flow.emovent.com.resetPassword

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import flow.emovent.com.R
import flow.emovent.com.modules.server
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var emailV:EditText
    lateinit var resetBtn:Button
    lateinit var backBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        emailV=findViewById(R.id.email)
        resetBtn=findViewById(R.id.btn_reset_password)
        backBtn=findViewById(R.id.btn_back)
        window.statusBarColor= Color.parseColor("#ffffff")
    }

    override fun onStart() {
        super.onStart()
        resetBtn.setOnClickListener {
            val email=emailV.text.toString().trim()
            if(email.isEmpty()){emailV.error="can't be embty"; return@setOnClickListener}
            resetBtn.isEnabled=false
            backBtn.isEnabled=false
            resetBtn.text="Loading"
            class connect:AsyncTask<String?,String?,String?>(){
                var response:StringBuffer?=null
                override fun doInBackground(vararg p0: String?): String? {
                    val url=URL("http://$server/reqrestoreurl?email=$email")
                    return try{
                        with(url.openConnection() as HttpURLConnection){
                            connectTimeout=15000
                            requestMethod="POST"
                            if(responseCode==200){
                                inputStream.bufferedReader().use{
                                    var inputLine=it.readLine()
                                    while (inputLine != null) {
                                        response = StringBuffer()
                                        response?.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                    it.close()
                                }
                            }
                        }
                        response.toString()
                    }catch (e:Exception){null}
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                    when(result){
                        "done"-> Toast.makeText(this@ResetPasswordActivity,"check your email inbox",Toast.LENGTH_LONG).show()
                        null-> Toast.makeText(this@ResetPasswordActivity,"something went wrong",Toast.LENGTH_LONG).show()
                        else->Toast.makeText(this@ResetPasswordActivity,result,Toast.LENGTH_LONG).show()
                    }
                    resetBtn.isEnabled=true
                    backBtn.isEnabled=true
                    resetBtn.text="Reset Password"
                }
            }
            connect().execute()
        }
        backBtn.setOnClickListener{finish()}
    }
}
