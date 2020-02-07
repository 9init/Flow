package flow.emovent.com.resetPassword

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import flow.emovent.com.LoginActivity
import flow.emovent.com.R
import flow.emovent.com.modules.server
import java.net.HttpURLConnection
import java.net.URL

class PWChanges : AppCompatActivity() {
    lateinit var password:EditText
    lateinit var cpassword:EditText
    lateinit var confirmBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pwchanges)
        window.statusBarColor= Color.parseColor("#ffffff")

        password=findViewById(R.id.password)
        cpassword=findViewById(R.id.cpassword)
        confirmBtn=findViewById(R.id.confirmBtn)
    }

    override fun onStart() {
        super.onStart()
        try {
            val queries= intent.data
            val id=queries?.getQueryParameter("url")
            if(id.isNullOrEmpty())this@PWChanges.finish()

            confirmBtn.setOnClickListener {
                val pass=password.text.toString()
                val cpass=cpassword.text.toString()
                if(pass!=cpass){
                    cpassword.error="check your password"
                    return@setOnClickListener
                }
                val passW=password.text.toString()
                confirmBtn.isEnabled=false
                confirmBtn.text="Loading"
                var response:StringBuffer?=null
                class connect:AsyncTask<String?,String?,String?>(){
                    override fun doInBackground(vararg p0: String?): String? {
                        val url=URL("http://$server/restore?url=$id&password=$passW")
                        return try {
                            with(url.openConnection() as HttpURLConnection){
                                connectTimeout=15000
                                requestMethod="POST"
                                if(responseCode==200){
                                    inputStream.bufferedReader().use{
                                        var inputLine=it.readLine()
                                        while (inputLine!=null){
                                            response= StringBuffer()
                                            response?.append(inputLine)
                                            inputLine=it.readLine()
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
                            null ->{
                                Toast.makeText(this@PWChanges,"something went wrong",Toast.LENGTH_LONG).show()
                                confirmBtn.text="reset"
                                confirmBtn.isEnabled=true
                            }
                            "done!"->{
                                Toast.makeText(this@PWChanges,"Done!",Toast.LENGTH_LONG).show()
                                confirmBtn.text="Login Page"
                                confirmBtn.setOnClickListener {
                                    val intent= Intent(this@PWChanges, LoginActivity::class.java)
                                    startActivity(intent)
                                    overridePendingTransition(0,0)
                                    finish()
                                }
                                confirmBtn.isEnabled=true
                            }
                            else -> {
                                Toast.makeText(this@PWChanges,result,Toast.LENGTH_LONG).show()
                                confirmBtn.text="reset"
                                confirmBtn.isEnabled=true
                            }
                        }

                    }
                }
                connect().execute()
            }
        }catch (e:Exception){this@PWChanges.finish()}
    }
}
