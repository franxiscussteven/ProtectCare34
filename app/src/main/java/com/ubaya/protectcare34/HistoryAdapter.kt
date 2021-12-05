package com.ubaya.protectcare34

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.history_card.view.*
import org.json.JSONObject

class HistoryAdapter(val histories:ArrayList<History>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryListViewHolder>() {
    class HistoryListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.history_card, parent, false)
        return HistoryListViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryListViewHolder, position: Int) {
        val history = histories[position]
        with(holder.view) {
            textLocation.text = history.place
            val checkin = history.checkin
            textCheckIn.text = "Check in: $checkin"
            val checkout = history.checkout
            if(checkout == "null")
                textCheckOut.text = "Check out: Not yet checked out"
            else
                textCheckOut.text = "Check out: $checkout"
            val num_vac = history.num
            if(num_vac==1)
                cardHistory.setBackgroundColor(Color.parseColor("Yellow"))
            else if(num_vac==2)
                cardHistory.setBackgroundColor(Color.parseColor("Green"))
        }
    }

    override fun getItemCount() = histories.size
}