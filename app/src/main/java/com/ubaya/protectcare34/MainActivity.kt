package com.ubaya.protectcare34

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val fragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME).toString()
        var username = GlobalData.username
        user(username)

        if(GlobalData.status == "checkin")
            fragments.add(CheckoutFragment())
        else
            fragments.add(CheckinFragment())
        fragments.add(HistoryFragment())
        fragments.add(ProfileFragment())

        viewPager.adapter = MyAdapter(this, fragments)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.selectedItemId = bottomNav.menu[position].itemId
            }
        })

        bottomNav.setOnItemSelectedListener {
            viewPager.currentItem = when(it.itemId) {
                R.id.itemCheckin -> 0
                R.id.itemHistory -> 1
                R.id.itemProfile -> 2
                else -> 0
            }
            true
        }
    }

    fun user(username: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://ubaya.fun/native/160719019/get_user.php"
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("checkparams", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        var placeObj = data.getJSONObject(i)
                        with(placeObj) {
                            GlobalData.user.id = getInt("id")
                            GlobalData.user.name = getString("name")
                            GlobalData.user.vaccine = getInt("vaccine")
                        }
                    }
                    var id = GlobalData.user.id
                    check(id)
                }
            },
            Response.ErrorListener {
                Log.d("paramserror", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                return params
            }
        }
        queue.add(stringRequest)
    }

    fun check(user: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://ubaya.fun/native/160719019/check.php"
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("checkparams", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        var placeObj = data.getJSONObject(i)
                        with(placeObj) {
                            GlobalData.checkout.id = getInt("id")
                            GlobalData.checkout.checkin = getString("checkin")
                            GlobalData.checkout.placename = getString("name")
                            GlobalData.checkout.num = getInt("num_vaccines")
                        }
                    }
                    textPlace?.text = GlobalData.checkout.placename
                    textCheckin?.text = "Check in time: " + GlobalData.checkout.checkin

                    if(GlobalData.checkout.num == 1)
                        cardCheckout.setBackgroundColor(Color.parseColor("Yellow"))
                    else if(GlobalData.checkout.num == 2)
                        cardCheckout?.setBackgroundColor(Color.parseColor("Green"))
                }
            },
            Response.ErrorListener {
                Log.d("paramserror", it.message.toString())
            }
        )
        {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["users_id"] = user.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }
}