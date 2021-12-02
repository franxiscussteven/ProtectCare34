package com.ubaya.protectcare34

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val fragments: ArrayList<Fragment> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var num = intent.getStringExtra(CheckinFragment.EXTRA_CHECK).toString()

        if(num == "masuk")
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

        var username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME).toString()
        user(username)
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
                        Log.d("usercheck", GlobalData.user.id.toString())
                        Log.d("usercheck", GlobalData.user.name.toString())
                        Log.d("usercheck", GlobalData.user.vaccine.toString())
                    }
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
}