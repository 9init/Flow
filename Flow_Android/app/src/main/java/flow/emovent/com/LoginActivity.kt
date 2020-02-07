package flow.emovent.com

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import flow.emovent.com.modules.isNullOrEmpty
import flow.emovent.com.modules.server
import flow.emovent.com.profile.ProfileService
import flow.emovent.com.register.SignupActivity
import flow.emovent.com.resetPassword.ResetPasswordActivity
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {
    lateinit var emailV:EditText
    lateinit var passV:EditText
    lateinit var loginV:Button
    lateinit var btnSignUp:Button
    lateinit var resetBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emailV=findViewById(R.id.email)
        passV=findViewById(R.id.password)
        loginV=findViewById(R.id.btn_login)
        btnSignUp=findViewById(R.id.sign_in_button)
        resetBtn=findViewById(R.id.btn_reset_password)

        window.navigationBarColor=Color.parseColor("#2962ff")
        window.statusBarColor=Color.parseColor("#ffffff")
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        loginV.setOnClickListener {

            if(isNullOrEmpty(emailV.text.toString()) || isNullOrEmpty(passV.text.toString())){
                Toast.makeText(this@LoginActivity,"Something is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginV.isEnabled=false
            loginV.text="Loading..."
            class ConnectToDB : AsyncTask<String?, String?, String?>() {
                var response:String?=null
                var rescode:Int?=null
                @SuppressLint("WrongThread")
                override fun doInBackground(vararg params: String?): String? {
                    val email=URLEncoder.encode(emailV.text.toString(),"UTF-8")
                    val passw=URLEncoder.encode(passV.text.toString(),"UTF-8")
                    //val objects="login?uInput=$email&password=$passw"
                    val url = URL("http://$server/login")

                    return try {
                        with(url.openConnection() as HttpURLConnection) {
                            readTimeout=20*1000
                            addRequestProperty("uInput", email)
                            addRequestProperty("password", passw)
                            requestMethod = "POST"  // default is GET
                            //Log.d("Connection","Sent 'POST' request to URL : $url; Response Code : $responseCode")

                            rescode=responseCode
                            response= if (responseCode == HttpURLConnection.HTTP_OK) inputStream.bufferedReader().readLine()
                            else errorStream.bufferedReader().readLine()
                            response
                        }
                    }catch (t:Throwable){
                        null
                    }

                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                    when(rescode){
                        200->{
                            Log.d("login", "result : $result")
                            val intent=Intent(this@LoginActivity, ProfileService::class.java)
                            intent.putExtra("info",result)
                            startActivity(intent)
                            overridePendingTransition(0,0)
                            finish()
                        }
                        else ->{
                            Toast.makeText(this@LoginActivity, result,Toast.LENGTH_LONG).show()
                        }
                    }
                    loginV.isEnabled=true
                    loginV.text="Login"
                }
            }
            ConnectToDB().execute()




        }
        resetBtn.setOnClickListener {
            ResetPasswordActivity().finish()
            val intent=Intent(this@LoginActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }
        btnSignUp.setOnClickListener {
            SignupActivity().finish()
            val intent=Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }
    }
}
