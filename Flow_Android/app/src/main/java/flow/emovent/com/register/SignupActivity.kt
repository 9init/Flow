package flow.emovent.com.register

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import flow.emovent.com.LoginActivity
import flow.emovent.com.R
import flow.emovent.com.resetPassword.ResetPasswordActivity
import flow.emovent.com.modules.server
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
@SuppressLint("SetTextI18n")
class SignupActivity : AppCompatActivity() {
    lateinit var nameE:EditText
    lateinit var passE:EditText
    lateinit var userE:EditText
    lateinit var emailE:EditText
    lateinit var signup:Button
    lateinit var checkbox:CheckBox
    lateinit var resetBtn:Button
    lateinit var loginBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        window.navigationBarColor= Color.parseColor("#2962ff")
        window.statusBarColor= Color.parseColor("#ffffff")

        nameE=findViewById(R.id.Name)
        passE=findViewById(R.id.password)
        userE=findViewById(R.id.user_name)
        emailE=findViewById(R.id.email)
        signup=findViewById(R.id.sign_up_button)
        checkbox=findViewById(R.id.checkbox)
        resetBtn=findViewById(R.id.btn_reset_password)
        loginBtn=findViewById(R.id.login_button)
    }

    override fun onStart() {
        super.onStart()
        signup.isEnabled=false
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            signup.isEnabled = isChecked
        }
        signup.setOnClickListener {

            signup.text="Loading..."
            signup.isEnabled=false
            class signUp:AsyncTask<String?,String?,String?>(){
                val name=nameE.text.toString().trim().replace(" ","+")
                var user=userE.text.toString()
                val email=emailE.text.toString().trim()
                val passW=passE.text.toString()
                @SuppressLint("WrongThread")
                override fun doInBackground(vararg params: String?): String? {
                    if (checkEditTexts()){ return "something is missing" }

                    val url=URL("http://$server/register")
                    return try {
                        with(url.openConnection() as HttpURLConnection){
                            addRequestProperty("name", name)
                            addRequestProperty("user", user)
                            addRequestProperty("email",email)
                            addRequestProperty("password", passW)
                            connectTimeout = 15000
                            requestMethod = "POST"

                            /*
                            if (responseCode == 200) {
                                inputStream.bufferedReader().use {
                                    var inputLine = it.readLine()
                                    while (inputLine != null) {
                                        response = StringBuffer()
                                        response?.append(inputLine)
                                        inputLine = it.readLine()
                                    }
                                    it.close()
                                }
                                response.toString()
                            }else{
                                null
                            }
                            */
                            if(errorStream==null)inputStream.bufferedReader().readLine()
                            else errorStream.bufferedReader().readLine()
                        }
                    }catch (e:Exception){
                        null
                    }
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)
                    when(result){
                        "Thanx!"-> Toast.makeText(this@SignupActivity,result,Toast.LENGTH_LONG).show()
                        else-> Toast.makeText(this@SignupActivity,result,Toast.LENGTH_LONG).show()
                    }
                    signup.text="Register"
                    signup.isEnabled=true
                    checkbox.isChecked=false
                }
            }
            signUp().execute()
        }

        loginBtn.setOnClickListener{
            LoginActivity().finish()
            val intent=Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }
        resetBtn.setOnClickListener {
            ResetPasswordActivity().finish()
            val intent=Intent(this@SignupActivity, ResetPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

    }

    fun checkEditTexts(): Boolean {
        return emailE.text.isEmpty() || passE.text.isEmpty() || nameE.text.isEmpty() || userE.text.isEmpty()

    }


}



