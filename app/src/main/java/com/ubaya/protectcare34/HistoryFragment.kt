package com.ubaya.protectcare34

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_history.view.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var histories: ArrayList<History> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun updateList() {
        val linearLayoutManager = LinearLayoutManager(activity)
        view?.historyView?.let {
            it.layoutManager = linearLayoutManager
            it.setHasFixedSize(true)
            it.adapter = HistoryAdapter(histories)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onResume() {
        super.onResume()

        histories.clear()
        val queue = Volley.newRequestQueue(activity)
        val url = "https://ubaya.fun/native/160719019/get_history.php"
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        var playObj = data.getJSONObject(i)
                        with(playObj) {
                            var history = History(
                                getInt("id"),
                                getString("name"),
                                getString("checkin"),
                                getString("checkout"),
                                getInt("num_vaccines")
                            )
                            histories.add(history)
                        }
                    }
                    Log.d("playlistcheck", histories.toString())
                    updateList()
                }
            },
            Response.ErrorListener {
                Log.d("apierror", it.message.toString())
            }
        )
        {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["users_id"] = GlobalData.user.id.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}