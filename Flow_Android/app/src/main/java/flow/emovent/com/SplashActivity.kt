package flow.emovent.com

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import flow.emovent.com.modules.server
import flow.emovent.com.profile.ProfileService
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.navigationBarColor= Color.parseColor("#ffffff")
        window.statusBarColor= Color.parseColor("#ffffff")
    }

    override fun onStart() {
        super.onStart()
        class Connect:AsyncTask<Int?,Int?,Int?>(){
            override fun doInBackground(vararg params: Int?): Int? {
                val url=URL("http://$server/check")
                return try {
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "POST"

                        responseCode
                    }
                }catch (e:Exception){null}
            }

            override fun onPostExecute(result: Int?) {
                super.onPostExecute(result)
                when (result) {
                    200 -> {
                        val intent=Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0,0)
                        finish()
                    }
                    else ->{
                        AlertDialog.Builder(this@SplashActivity).apply {
                            setCancelable(false)
                            setTitle("Request Timeout!")
                            setPositiveButton("Retry"){ _, _ ->
                                Connect().execute()
                            }
                            setNegativeButton("close"){ _, _ ->
                                this@SplashActivity.finish()
                            }
                            setOnCancelListener{Connect().execute()}
                            create()
                            show()
                        }
                    }
                }
            }
        }
        Connect().execute()
    }
}
