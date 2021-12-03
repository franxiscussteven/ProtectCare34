package com.ubaya.protectcare34

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    companion object {
        val EXTRA_USERNAME = "EXTRA_USERNAME"
        val EXTRA_STATUS = "EXTRA_STATUS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener {
            var username = editUsername.text.toString()
            var password = editPassword.text.toString()
            var status = ""

            val queue = Volley.newRequestQueue(this)
            val url = "https://ubaya.fun/native/160719019/login.php"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener {
                    Log.d("checkparams", it)
                    val obj = JSONObject(it)
                    if(obj.getString("result") == "success") {
                        val data = obj.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            var placeObj = data.getJSONObject(i)
                            with(placeObj) {
                                status = getString("status")
                            }
                        }
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(EXTRA_USERNAME, username)
                        intent.putExtra(EXTRA_STATUS, status)
                        startActivity(intent)
                        finish()
                    }
                    else
                        Toast.makeText(this, "Incorrect Username Or Password!", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener {
                    Log.d("paramserror", it.message.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["password"] = password

                    return params
                }
            }
            queue.add(stringRequest)
        }
    }
}