package com.ubaya.protectcare34

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_checkin.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckinFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val fragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val queue = Volley.newRequestQueue(activity)
        val url = "https://ubaya.fun/native/160719019/get_places.php"
        val stringRequest = StringRequest(
            Request.Method.POST,
            url,
            Response.Listener {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        var placeObj = data.getJSONObject(i)
                        with(placeObj) {
                            var place = Place(
                                getInt("id"),
                                getString("name"),
                                getString("code")
                            )
                            GlobalData.places.add(place)
                        }
                    }
                }
            },
            Response.ErrorListener {
                Log.d("apierror", it.message.toString())
            }
        )
        queue.add(stringRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = activity?.let { ArrayAdapter(it, R.layout.myspinner_layout, GlobalData.places) }
        adapter?.setDropDownViewResource(R.layout.myspinner_item_layout)
        spinnerPlace.adapter = adapter
        adapter?.notifyDataSetChanged()

        buttonCheckin.setOnClickListener {
            var place = spinnerPlace.selectedItem.toString()
            var code = editCode.text.toString()
            var id = spinnerPlace.selectedItemPosition.toString()

            val queue = Volley.newRequestQueue(context)
            val url = "https://ubaya.fun/native/160719019/checkin.php"
//            val url = "https://ubaya.fun/native/160719019/test.php"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener {
                    Log.d("checkparams", it)
                    val obj = JSONObject(it)
                    if(obj.getString("result") == "success") {
                        Toast.makeText(context, "Berhasil!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra(EXTRA_CHECK, "masuk")
                        startActivity(intent)
                        activity?.finish()
                    }
                    else
                        Toast.makeText(context, "Incorrect Unique Code!", Toast.LENGTH_SHORT).show()
                },
                Response.ErrorListener {
                    Toast.makeText(context, "You must vaccinated at least once!", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["name"] = place
                    params["code"] = code
                    params["vaccine"] = GlobalData.user.vaccine.toString()
                    params["users_id"] = GlobalData.user.id.toString()
                    params["places_id"] = id

                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

    companion object {
        val EXTRA_CHECK = "EXTRA_CHECK"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CheckinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CheckinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}